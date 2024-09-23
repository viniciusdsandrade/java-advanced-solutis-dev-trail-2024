package br.com.agilizeware.file.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.util.AppPropertiesService;
import br.com.agilizeware.util.Util;

@Component("fileSystemStorageIf")
public class FileSystemStorage {

	public static final String PATH_FILE = "agilize.file.physical.path";
	//private static final String PATH = "/var/file/uploads";
	
    private final Path rootLocation;

    @Autowired
    public FileSystemStorage(AppPropertiesService appPropertiesService) {
        this.rootLocation = Paths.get(appPropertiesService.getPropertyString(PATH_FILE));
        //this.rootLocation = Paths.get(PATH);
    }

    public Path store(MultipartFile file) {
    	Path ret = null;
    	try {
            if (file.isEmpty()) {
            	throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.FILE_EMPTY, file.getOriginalFilename());
                //throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            ret = this.rootLocation.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), ret);
        } catch (IOException e) {
        	throw new AgilizeException(HttpStatus.INTERNAL_SERVER_ERROR.ordinal(), ErrorCodeEnum.ERROR_CRUD_FILE, e, file.getOriginalFilename());
            //throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
        return ret;
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
        	throw new AgilizeException(HttpStatus.INTERNAL_SERVER_ERROR.ordinal(), ErrorCodeEnum.ERROR_READ_FILE, e);
            //throw new StorageException("Failed to read stored files", e);
        }

    }

    public Path load(String path, String filename) {
    	if(Util.isNotNull(path)) {
    		return rootLocation.resolve(path + java.io.File.separator + filename);
    	}
    	return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String path, String filename) {
        try {
            Path file = load(path, filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
            	throw new AgilizeException(HttpStatus.INTERNAL_SERVER_ERROR.ordinal(), ErrorCodeEnum.ERROR_READ_FILE);
                //throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
        	throw new AgilizeException(HttpStatus.INTERNAL_SERVER_ERROR.ordinal(), ErrorCodeEnum.ERROR_READ_FILE, e);
            //throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
        	throw new AgilizeException(HttpStatus.INTERNAL_SERVER_ERROR.ordinal(), ErrorCodeEnum.ERROR_INIT_FILE, e);
            //throw new StorageException("Could not initialize storage", e);
        }
    }
    
    public Path storeFile(File file, String label) {
    	Path ret = null;
    	try {

    		if (!file.exists()) {
            	throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.FILE_EMPTY, file.getAbsolutePath());
                //throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }  
    		
    		ret = Paths.get(rootLocation+java.io.File.separator+label);
        	Files.createDirectories(ret);
            
            ret = ret.resolve(file.getName());
            FileInputStream input = new FileInputStream(file);
            Files.copy(input, ret);
            
        } catch (IOException e) {
        	throw new AgilizeException(HttpStatus.INTERNAL_SERVER_ERROR.ordinal(), ErrorCodeEnum.ERROR_CRUD_FILE, e, file.getAbsolutePath());
            //throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
        return ret;
    }
}