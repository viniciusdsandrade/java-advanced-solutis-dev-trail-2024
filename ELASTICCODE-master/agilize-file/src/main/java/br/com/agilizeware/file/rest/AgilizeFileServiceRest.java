package br.com.agilizeware.file.rest;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.file.component.FileSystemStorage;
import br.com.agilizeware.file.repository.FileDaoImpl;
import br.com.agilizeware.model.File;
import br.com.agilizeware.rest.ServiceRestEntityAb;
import br.com.agilizeware.util.AppPropertiesService;
import br.com.agilizeware.util.Util;

@RestController
@RequestMapping("/file")
@Service
public class AgilizeFileServiceRest extends ServiceRestEntityAb<File, Long> {
	
	@Autowired
	private AppPropertiesService appPropertiesService;
	
	@Autowired
	private FileSystemStorage fileSystemStorageIf;
	
	@Autowired
	private FileDaoImpl fileDaoIf;
	
	//private static int MAXBUF = 8192; // 8 Mb
	
	@Override
	protected DaoAB<File, Long> definirDao() {
		return fileDaoIf;
	}

	private String getRandomNameFile() {
		return Util.onlyAlphaNumeric(Util.generateUuid());
	}
	
	@RequestMapping(value = { "/{path}/{filename:.+}" }, method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable("path") String path,
			@PathVariable("filename") String filename) throws IOException {
		
		 Resource file = fileSystemStorageIf.loadAsResource(path, filename);
	     return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""+file.getFilename()+"\"") //, "attachment; filename=\""+file.getFilename()+"\"")
	                .body(file);
	}
	
	/**
	 * Recebe path de arquivo temporário, grava arquivo no path destino, exclui
	 * path temporario.
	 * 
	 * @param fileDTO
	 * @return
	 */
	@RequestMapping(value="/saveTempFile", method = RequestMethod.POST)
    @ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
	public RestResultDto<List<File>> saveTempFile(@RequestBody List<File> receiveds) {
		
		RestResultDto<List<File>> result = new RestResultDto<List<File>>();
		
		List<File> files = new ArrayList<File>(1);
		//String path = appPropertiesService.getPropertyString(FileSystemStorage.PATH_FILE);
		File saved = null;
		/*byte[] buffer = null;
		FileInputStream in = null;
		FileOutputStream out = null;
		String fileName = null;*/
		Date dtAtual = Util.obterDataHoraAtual();
		
		for (File file : receiveds) {
			
			saved = new File();
			saved.setContentType(file.getContentType());
			java.io.File fileIn = new java.io.File(file.getPathPhysical() + java.io.File.separator + file.getName());

			String label = file.getNmEntity() + java.io.File.separator + getRandomNameFile();
			Path p = fileSystemStorageIf.storeFile(fileIn, label);
			
			/*String dirPath = path + java.io.File.separator + file.getNmEntity() + "_" + getRandomNameFile();
			java.io.File fDir = new java.io.File(dirPath);
			fDir.mkdirs();*/
			
			//saved.setPathPhysical(dirPath);
			saved.setPathPhysical(p.toAbsolutePath().getParent().toString());
			
			/*in = null;
			out = null;
			fileName = null;
			
			try {
				buffer = new byte[MAXBUF];
				in = new FileInputStream(fileIn);
				fileName = dirPath + java.io.File.separator + fileIn.getName();
				out = new FileOutputStream(fileName);
				int readBytes = 0;
				while ((readBytes = in.read(buffer, 0, MAXBUF)) != -1) {
					out.write(buffer, 0, readBytes);
				}*/

				saved.setName(fileIn.getName()); 
				
				/*out.flush();
				out.close();
				fileIn.delete();
				in.close();*/
				
				saved.setPathLogical(appPropertiesService.getPropertyString("agilize.file.logical.path"));
				saved.setDtCreate(dtAtual);
				saved.setIdUserCreate(file.getIdUserCreate());
				saved.setNmEntity(file.getNmEntity());
				
				saved = fileDaoIf.save(saved);
				files.add(saved);
				
			/*} catch (Exception e) {
				log.error("Erro na gravação do arquivo", e);
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_READ_TMP_FILE, e)));
				return result;
			}*/
		}
		
		result.setSuccess(true);
		result.setData(files);
		return result;
	}
	
	
	@RequestMapping(value = { "/upload/{nmEntity}/{idUser}" }, method = RequestMethod.POST)
	@ResponseBody
	public RestResultDto<List<File>> upload(@PathVariable("nmEntity") String nmEntity,
			@PathVariable("idUser") Long idUser,
			MultipartHttpServletRequest request, ServletResponse response) throws IOException {
		
		RestResultDto<List<File>> result = new RestResultDto<List<File>>();
		String logicalPath = appPropertiesService.getPropertyString("agilize.file.logical.path");
		
		List<File> fileList = new ArrayList<File>(1);
		
		File uploadFileDto = null;
        Date dtAtual = Util.obterDataHoraAtual();
				
		try{
	
			//DefaultMultipartHttpServletRequest multipartHttpServletRequest = (DefaultMultipartHttpServletRequest)request;
			//multipartHttpServletRequest.getFileMap();
			
	        String dirTemp = System.getProperty("java.io.tmpdir");

	        // Parse the request
            //Map<String, MultipartFile> fileMap = multipartHttpServletRequest.getFileMap();
            Map<String, MultipartFile> fileMap = request.getFileMap();

            for(Map.Entry<String, MultipartFile> multipartFileEntry: fileMap.entrySet()) {
            	
            	uploadFileDto = new File();
                uploadFileDto.setDtCreate(dtAtual);
                uploadFileDto.setPathLogical(logicalPath);
                uploadFileDto.setNmEntity(nmEntity);

            	String dirPath = dirTemp + java.io.File.separator + "TEMP_" + Util.obterDataHoraAtual().getTime();
            	java.io.File dir = new java.io.File(dirPath);
                if (dir.exists() == false) {
                    dir.mkdirs();
                }
                String nameFile = Normalizer.normalize(multipartFileEntry.getValue().getOriginalFilename(),Normalizer.Form.NFD).
                    	replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                java.io.File fTemp = new java.io.File(dirPath + java.io.File.separator + nameFile);
                
                OutputStream out = new FileOutputStream(fTemp, true); // append
                uploadFileDto.setPathPhysical(FilenameUtils.getFullPath(fTemp.getAbsolutePath()));

                BufferedOutputStream stream = new BufferedOutputStream(out);
                FileCopyUtils.copy(multipartFileEntry.getValue().getInputStream(), stream);
                stream.close();
                uploadFileDto.setContentType(multipartFileEntry.getValue().getContentType());
                uploadFileDto.setName(nameFile);
                uploadFileDto.setIdUserCreate(idUser);
                
                fileList.add(uploadFileDto);
            }
	        
	        //ObjectMapper mapper = new ObjectMapper();
	        result.setSuccess(true);
	        result.setData(fileList);
			String json = RestResultDto.getMapper().writeValueAsString(result);
	        
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
			ServletResponse resp = response;
			//Pega o cabeçalho accpet para tratamento de contenttype para funcionar no IE 9 e Opera
			String accpetHeader = request.getHeader("Accept");
			if(accpetHeader != null && accpetHeader.contains("application/json")) {
				((HttpServletResponse) resp).setContentType("application/json;charset=UTF-8");	
			} else {
				((HttpServletResponse) resp).setContentType("text/plain;charset=UTF-8");
			}
			
			((HttpServletResponse) resp).addHeader("Content-length", "" + json.length());
			// Acerto de header para IE8/9
			((HttpServletResponse) resp).addHeader("Content-Disposition",
					"inline; filename=\"" + uploadFileDto.getName() + "\"");
	
			baos.write(json.getBytes());
			response.getOutputStream().write(baos.toByteArray());
			response.getOutputStream().flush();
		
		} catch(Exception ex) {
			ex.printStackTrace();
			//Mensagem genérica de erro de upload de arquivo temporário
        	log.error("Erro no upload do arquivo temporário", ex);
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(
					new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_SAVE_TMP_FILE, ex)));
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = { "/fileById/{id}" }, method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Resource> getFileById(@PathVariable("id") Long id) throws IOException {
	
		File file = fileDaoIf.findOne(id);
		Resource resource = fileSystemStorageIf.loadAsResource(file.getPathPhysical(), file.getName());
		return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""+resource.getFilename()+"\"") //, "attachment; filename=\""+resource.getFilename()+"\"")
                .body(resource);
	}

	@RequestMapping(value = { "/findFileById/{id}" }, method = RequestMethod.GET)
	@ResponseBody
	public RestResultDto<File> findFileById(@PathVariable(required=true, value="id") Long id) throws IOException {
		RestResultDto<File> res = new RestResultDto<File>();
		File f = fileDaoIf.findOne(id);
		res.setData(f);
		res.setSuccess(true);
		return res;
	}
	
}
