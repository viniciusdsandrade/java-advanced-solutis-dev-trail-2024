angular.module('services.propriedades', ['localStorage']).service('PropriedadesCompartilhadas', ['$rootScope', '$store', function ($rootScope, $store) {
	
	var sesProp = {key: null, value: null, datePropSave: null};
	
	return {
		set: function(key,value) {
			sesProp.key = key;
			sesProp.value = value;
			sesProp.datePropSave = Date.now();
			$store.set(key,sesProp);
		},
		get: function(key) {
			sesProp = $store.get(key);
			if(sesProp){
				var qdtHours = calculateDiffHoursToDtAtual(sesProp.datePropSave);
	    		if(qdtHours > 2) {
	    			this.remove(key);
	    		}
				return sesProp.value;
			}
			return null;
		},
		remove: function(key) {
			$store.remove(key);
		}
	}
	
}]);