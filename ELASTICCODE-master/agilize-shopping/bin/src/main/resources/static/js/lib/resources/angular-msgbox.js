angular.module("msgbox", ["ui.bootstrap", "ui.bootstrap.modal"])
.factory("$msgbox", function($rootScope, $modal, $q){
	

    
    var scope = $rootScope.$new();
    
    return {
        show: function(msg, opts, hideCancel){
        	
        	var success_tpl;
        	opts = opts || {};
        	
        	if(opts && opts.mensagens) {
        		
        		success_tpl = '<div class="modal-header" data-ng-show="{{title.length}}"> <h4 class="modal-title">{{title}}</h4></div><div class="modal-body" style="font-size: 18px">';
        		angular.forEach(opts.mensagens, function(item){
        			success_tpl = success_tpl + item + '<br>';
        		});
        		success_tpl = success_tpl + '</div><div class="modal-footer"> <button class="btn btn-primary" data-ng-click="ok($event)">Ok</button>';
        		if(hideCancel){
        			success_tpl = success_tpl + '</div>';
        		}
        		else {
        			success_tpl = success_tpl + '<button class="btn btn-default" data-ng-click="cancel($event)">Cancel</button></div>';
        		}
        	}
        	else {
            	if(hideCancel){
            		success_tpl = '<div class="modal-header" data-ng-show="{{title.length}}"> <h4 class="modal-title">{{title}}</h4></div><div class="modal-body" style="font-size: 18px">{{message}}</div><div class="modal-footer"> <button class="btn btn-primary" data-ng-click="ok($event)">Ok</button></div>';
            	}else{
            		success_tpl = '<div class="modal-header" data-ng-show="{{title.length}}"> <h4 class="modal-title">{{title}}</h4></div><div class="modal-body" style="font-size: 18px">{{message}}</div><div class="modal-footer"> <button class="btn btn-primary" data-ng-click="ok($event)">Ok</button> <button class="btn btn-default" data-ng-click="cancel($event)">Cancel</button></div>';
            	}
        	}
        	
        	
            var defer = $q.defer();
            
            
            $modal.open({
                template: success_tpl,
                scope: scope,
                controller: function($scope, $modalInstance){
                    $scope.title = opts.title;
                    $scope.message = msg;
                    
                    $scope.ok = function($event){
                        $event.preventDefault();
                        $modalInstance.close();
                        defer.resolve();
                    };
                    $scope.cancel = function($event){
                        $event.preventDefault();
                        $modalInstance.close();
                        defer.reject();
                    };
                },
                size: "sm",
                backdrop: "static",
                keyboard: false
            });
            return defer.promise;
        }
    }    
});