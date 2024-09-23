// servico para controle do upload plugin jquery
// trata-se de uma diretiva de atributo, porem foi criado como serviço.
// Utilizada como exemplo : data-file-upload="options" 
// EX: <form id="fileupload" action="" method="POST" enctype="multipart/form-data" data-ng-controller="DemoFileUploadController" data-file-upload="options" data-ng-class="{'fileupload-processing': processing() || loadingFiles}">

angular.module('service.fileUpload', ['services.localizedMessages']).factory('FileUpload', ['$http', '$rootScope', function ($http, $rootScope) {
	var upOptions = {};
	var upQueue = [];
	var clearFiles;
	var returnDto;
	
	var messages = { maxNumberOfFiles : 'Excedeu número máximo de arquivos',
					 acceptFileTypes : 'Arquivo de tipo inválido', 
					 maxFileSize : 'tamanho limite excedido', 
					 minFileSize : 'Arquivo muito pequeno'
	    };
	
	/*var messages = { 
		maxNumberOfFiles : $rootScope.getMessage('lbl.general.tooManyFiles'),
		acceptFileTypes : $rootScope.getMessage('lbl.general.invalidFileType'), 
		maxFileSize : $rootScope.getMessage('lbl.general.fileTooBig'), 
		minFileSize : $rootScope.getMessage('lbl.general.fileTooSmall')
	};*/
	
	function FileUpload() { }
	
	FileUpload.setOptions = function(options) {
		upOptions = options;		
		upOptions.messages = messages;
	};
	FileUpload.getOptions = function() {
		return upOptions;
	};
	
	FileUpload.setQueue = function(queue) {
		upQueue = queue;
	};
	FileUpload.getQueue = function() {
		return upQueue;
	};
	
	FileUpload.setClearFiles = function(clear) {
		clearFiles = clear;
	};
	FileUpload.getClearFiles = function() {
		return clearFiles;
	};
	
	FileUpload.setReturnDto = function(retDto) {
		returnDto = retDto;
	};
	FileUpload.getReturnDto = function() {
		return returnDto;
	};

	return FileUpload;
}]);

// O scope deste controller é o mesmo (compartilhado) com o controller da tela que possui este
// algum eventual metodo criado neste scope poderá ser chamado ou criado no controller chamador
angular.module('service.fileUpload').controller('DemoFileUploadController', ['$scope', '$http', 'FileUpload','$rootScope', demoFileCtrlImpl]);
function demoFileCtrlImpl ($scope, $http, FileUpload, $rootScope) {
	
	var upOptions = FileUpload.getOptions();
	var upQueue = FileUpload.getQueue();
	if (!upOptions) {
		upOptions = {url:"/pathto/upload"};
	}
    $scope.options = upOptions;
    $scope.loadingFiles = false;
    $scope.queue = [];
    $scope.clearFiles = FileUpload.getClearFiles();
    $scope.disableUpload = 1;
    
    FileUpload.setQueue($scope.queue);
    
    /*$rootScope.$on('omni.fil.upload.change.url',function(event, pOption){
    	event.preventDefault();
    	$scope.options = pOption;
    });*/
    
    // Se foi declarado metodo "onClearFiles" no html, por exemplo :
    // <input type="file" name="files[]" multiple ng-disabled="disabled" data-ng-click="onClearFiles()">
    // Entao : limpa o array de files.
    $scope.onClearFiles = function() {
    	var clear = FileUpload.getClearFiles();
    	var queue = FileUpload.getQueue();
    	FileUpload.setQueue(queue);
        $scope.disableUpload = 0;
    	if (queue && clear) {
    		$scope.queue = [];
    		for (var i = 0; i < queue.length; i++) {
    			var file = queue[i];
    			if (file.$cancel) {
    				file.$cancel();
    			}
    		}
    	}
    };
    
}

angular.module('service.fileUpload').controller('FileDestroyController', ['$scope', '$http', demoDestroyCtrlImpl]);
function demoDestroyCtrlImpl ($scope, $http) {

	  var file = $scope.file,
	      state;
	  if (file.url) {
	      file.$state = function () {
	          return state;
	      };
	      file.$destroy = function () {
	          state = 'pending';
	          return $http({
	              url: file.deleteUrl,
	              method: file.deleteType
	          }).then(
	              function () {
	                  state = 'resolved';
	                  $scope.clear(file);
	              },
	              function () {
	                  state = 'rejected';
	              }
	          );
	      };
	  } else if (!file.$cancel && !file._index) {
	      file.$cancel = function () {
	          $scope.clear(file);
	      };
	  }
	
}

angular.module('service.fileUpload').directive('fileUploadDirective', ['$http', 'localizedMessages', function ($http, localizedMessages) {
	  return {
  	    restrict:'E',
  	    scope: {
  	        protectSave: '=',
  	        showDisplayName: '=',
  	    },
  	    replace: true,
  	    require: '^form',
  	    templateUrl: '/js/lib/file/views/file-upload-tpl.html',
  	    controller:'DemoFileUploadController',  
  	    link: function($scope, element, attrs, $http) {
  	    	$scope.enableUploadClick = function(){
  	    		$scope.disableUpload = 0;
  	    		/*$scope.$apply(function() {
  	    	        console.log("update time clicked");
  	    	        $scope.disableUpload = 0;
  	    	    });*/
  	        };
  	    	$scope.getMessage = function(key) {
  	    		return localizedMessages.get(key);
  	        };
  	    	
  	      //Tratando o retorno do upload
      	  $scope.onUploadResponse = function(data) {
      		  var onUploadResponse = $scope.$parent.onUploadResponse;
      		  
      		  if(onUploadResponse){
      			  onUploadResponse(data);
      		  }
	      		$scope.queue = [];
	            $scope.disableUpload = 1;
	            
	            $scope.disabledClass = function(){
	            	if($scope.disableUpload == 0){
	            		return "disabledNo"
	            	}else{
	            		return "disabled"
	            	}
	            }
            
	            return;
      	  };
  	    }
  	}
 }]);
