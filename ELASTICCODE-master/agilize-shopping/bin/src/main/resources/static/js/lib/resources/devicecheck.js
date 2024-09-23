(function() {
  
  var DeviceCheck = {

    init : function() {
      this.userAgent = navigator.userAgent.toLowerCase();
      this.mobileRegEx = (/iphone|ipod|android|blackberry|opera|mini|windows\sce|palm|smartphone|iemobile/i);
      this.tabletRegEx = (/ipad|android 3|sch-i800|playbook|tablet|kindle|gt-p1000|sgh-t849|shw-m180s|a510|a511|a100|dell streak|silk/i);

      this.check();
    },
    
    device : { DEVICE_UNDEFINED: {id: 0, description: 'Não Definido', label: 'lbl.device.undefined'}, 
    	DEVICE_MOBILE: {id: 1, description: 'Mobile', label: 'lbl.device.mobile'},
    	DEVICE_TABLET: {id: 2, description: 'Tablet', label: 'lbl.device.tablet'},
    	DEVICE_DESKTOP: {id: 3, description: 'Desktop', label: 'lbl.device.desktop'}},

    findDeviceForId : function(id) {
    	if(!(id === undefined || id == null)) {
    		if(id === 0) {
    			return this.device.DEVICE_UNDEFINED;
    		}
    		else if(id === 1) {
    			return this.device.DEVICE_MOBILE;
    		}
    		else if(id === 2) {
    			return this.device.DEVICE_TABLET;
    		}
    		else {
    			return this.device.DEVICE_DESKTOP;
    		}
    	}
    	return this.device.DEVICE_UNDEFINED;
    },
    	
    check : function() {
      this.isMobile = this.mobileRegEx.test(this.userAgent);
      this.isTablet = this.tabletRegEx.test(this.userAgent);

      if(this.isMobile) {
        this.type = this.device.DEVICE_MOBILE;
      } else if(this.isTablet) {
        this.type = this.device.DEVICE_TABLET;
      } else {
        this.type = this.device.DEVICE_DESKTOP;
      }
    }

  };

  DeviceCheck.init();

  window.$.device = {
    type : DeviceCheck.type,
    userAgent : DeviceCheck.userAgent
  };

})();