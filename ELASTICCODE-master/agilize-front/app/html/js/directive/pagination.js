angular.module('directives.pagination', [])

.directive('pagination', function($rootScope) {
  return {
    restrict: 'E',
    scope: {
      numPages: '=',
      currentPage: '=',
      onSelectPage: '&',
      totalRows: '='
    },
    template:
		'<div class="pagination-small">' +      	
			'<div data-ng-if="totalRows == 0" class="span8 pag-not-found ml0 pl10">'+
				'<div class="ng-binding well well-info text-center" lang="pt">' + $rootScope.getMessage('lbl.general.noRecordsFound') + '.</div>'+
			'</div>'+
			'<div class="pagination">'+
				'<ul class="pull-right" data-ng-if="totalRows != 0">' +
					'<li ng-class="{disabled: noPrevious()}"><a href="" ng-click="selectFirst()"><i class="fa fa-angle-double-left"></i></a></li>' +
					'<li ng-class="{disabled: noPrevious()}"><a href="" ng-click="selectPrevious()"><i class="fa fa-angle-left"></i></a></li>' +
					'<li ng-repeat="page in pages" ng-class="{active: isActive(page)}"><a href="" ng-click="selectPage(page)">{{page}}</a></li>' +
					'<li ng-class="{disabled: noNext()}"><a href="" ng-click="selectNext()"><i class="fa fa-angle-right"></i></a></li>' +
					'<li ng-class="{disabled: noNext()}"><a href="" ng-click="selectLast()"><i class="fa fa-angle-double-right"></i></a></li>' +
				'</ul>' +
				'<span data-ng-if="totalRows != 0" class="label label-warning pull-right" style="margin: 6px">' + $rootScope.getMessage('lbl.general.recordsNumber') + ': {{totalRows}}</span>'+
			'<div>'+
		'</div>',
    replace: true,
    link: function(scope) {
      scope.$watch('numPages', function(value) {
    	scope.qtdButtons = 5; // qtd botoes <== Alterar aqui se desejar mais
        scope.pages = [];
        
        var max=scope.qtdButtons;
        if (value < max) {
        	max = value;
        }
        
        //for(var i=1;i<=value;i++) {
        for(var i=1;i<=max;i++) {
          scope.pages.push(i);
        }
        if ( scope.currentPage > value ) {
          scope.selectPage(value);
        }
      });
      scope.noPrevious = function() {
        return scope.currentPage === 1;
      };
      scope.noNext = function() {
        return scope.currentPage === scope.numPages;
      };
      scope.isActive = function(page) {
        return scope.currentPage === page;
      };

      scope.selectPage = function(page) {
        if ( ! scope.isActive(page) ) {
        	scope.calcButtons(page);
        	scope.currentPage = page;
        	scope.onSelectPage({ page: page});
        }
      };

      scope.selectPrevious = function() {
        if ( !scope.noPrevious() ) {
        	scope.calcButtons(scope.currentPage-1);
        	scope.selectPage(scope.currentPage-1);
        }
      };
      
      scope.selectNext = function() {
        if ( !scope.noNext() ) {
        	scope.calcButtons(scope.currentPage+1);
        	scope.selectPage(scope.currentPage+1);
        }
      };
      
      scope.selectFirst = function() {
          if ( !scope.noPrevious() ) {
        	  scope.calcButtons(1);
        	  scope.selectPage(1);
          }
        };
        
      scope.selectLast = function() {
            if ( !scope.noNext() ) {
            	scope.calcButtons(scope.numPages);
                scope.selectPage(scope.numPages);
              }
          };
          
      scope.calcButtons = function(pagTo) {
    	  var inic = parseInt(pagTo - (scope.qtdButtons / 2) + 1);
    	  if (inic < 1) { inic = 1; }
    	  var end = inic + scope.qtdButtons - 1;
    	  if (end > scope.numPages) {
    		  inic = inic - (end - scope.numPages);
    		  if (inic < 1) { inic = 1; }
    		  end = scope.numPages;
    		  }
    	  scope.pages = [];
		  for(var i=inic; i<end+1; i++) {
	          scope.pages.push(i);
	        }
          };
          
    }
  };
});