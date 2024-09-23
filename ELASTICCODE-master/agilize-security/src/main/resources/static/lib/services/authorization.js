angular.module("authorizationModule", []).factory("authorizationService", [
    "$q", "$rootScope", "APP_CONFIG", "uuid2", "$location", "$store",
    function ($q, $rootScope, APP_CONFIG, uuid2, $location, $store) {
        return {
            levelModel: {
                authorised: 0,
                loginRequired: 1,
                notAuthorised: 2
            },
            permissionCheck: function (loginRequired, requiredPermissions, permissionCheckType) {
                var result = {level: this.levelModel.notAuthorised, user: $store.get(APP_CONFIG.keySessionUser), loweredPermissions: [], hasPermission: true, permission: null};
                permissionCheckType = permissionCheckType || 'atLeastOne';
                var notLogged = (result.user === undefined || result.user.logged === false);
                if (loginRequired === true && notLogged) {
                    result.level = this.levelModel.loginRequired;
                } else if ((loginRequired === true && !notLogged) &&
                        (requiredPermissions === undefined || requiredPermissions.length === 0)) {
                    result.level = this.levelModel.authorised;
                } else if (requiredPermissions) {
                    result.loweredPermissions = [];
                    angular.forEach(result.user.user.authorities, function (permission) {
                        result.loweredPermissions.push(permission.authority.toLowerCase());
                    });
                    for (var i = 0; i < requiredPermissions.length; i += 1) {
                        permission = requiredPermissions[i].toLowerCase();
                        if (permissionCheckType === 'combinationRequired') {
                            result.hasPermission = result.hasPermission && result.loweredPermissions.indexOf(permission) > -1;
                            // if all the permissions are required and hasPermission is false there is no point carrying on
                            if (hasPermission === false) {
                                break;
                            }
                        } else if (permissionCheckType === 'atLeastOne') {
                        	result.hasPermission = result.loweredPermissions.indexOf(permission) > -1;
                            // if we only need one of the permissions and we have it there is no point carrying on
                            if (result.hasPermission) {
                                break;
                            }
                        }
                    }
                    result.level = result.hasPermission ? this.levelModel.authorised : this.levelModel.notAuthorised;
                }
                return result;
            },
            isAuthorized: function (loginRequired, requiredPermissions, permissionCheckType) {
                var verEval = this.permissionCheck(loginRequired, requiredPermissions, permissionCheckType);
                return verEval; 
                /*if (verEval.level === this.levelModel.authorised) {
                    return $q.when();
                } else if (verEval.level === this.levelModel.loginRequired) {
                    $timeout(function () {
                        // This code runs after the authentication promise has been rejected.
                        // Go to the log-in page
                        //$state.go('logInPage')})
                        $rootScope.login.open();
                    });
                } else {
                    $rootScope.message.show('lbl.msg.creation.account.bank');
                    
                    if(!isNullValue(PropriedadesCompartilhadas.oldUrl) &&
                    		(PropriedadesCompartilhadas.oldUrl.indexOf('portal/login', PropriedadesCompartilhadas.oldUrl.length - 'portal/login'.length) !== -1)) {
                    	$location.path('/portal');
                    	PropriedadesCompartilhadas.oldUrl = '';
                    	return;
                    }
                    
                    return $q.reject();
                }*/
            },
            encode: function (text) {
                var uuid = uuid2.newuuid();
                if (text.length > 0) {
                    var j = uuid.length - text.length;
                    while (j < 0) {
                        j = j + uuid.length;
                    }
                    var result = uuid + "-";
                    for (var i = 0; i < text.length; i++) {
                        if (j > uuid.length) {
                            j = 0;
                        }
                        result = result + formatNumForCode(text.charCodeAt(i) + uuid.charCodeAt(j));
                        j++;
                    }
                    return result;
                } else {
                    return text;
                }
            }
        };
    }
]);
function formatNumForCode(num) {
    var text = "" + num;
    while (text.length < 3) {
        text = "0" + text;
    }
    return text;
}