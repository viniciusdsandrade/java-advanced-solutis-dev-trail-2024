angular.module('background', ['restResource', 'ui.bootstrap'
]).directive('modifyBackGround', [function () {
        return {
            scope: {
                url: '=',
                css: '='
            },
            link: function (scope, elm, attrs, ctrl) {
                var urlValue = '';
                if (!isNullValue(scope.url)) {
                    urlValue = getHostServerFileAgilize() + scope.url.pathLogical;
                }
                var css = attrs.css;
                attrs.$observe('modifyBackGround', function () {
                    function setCss() {
                        if (!isNullValue(css)) {
                            elm.addClass(css);
                            if (!isNullValue(urlValue)) {
                                elm.css({
                                    'background-image': 'url(' + urlValue + ')'
                                });
                            }
                        } else {
                            elm.css({
                                'width': '100%',
                                'height': '100%',
                                'background-repeat': 'no-repeat',
                                'background-size': 'cover'
                            });
                        }
                        if (!isNullValue(urlValue)) {
                            elm.css({
                                'background-image': 'url(' + urlValue + ')'
                            });
                        }
                    }
                    setCss();
                    scope.$watch('url', function () {
                        if (!isNullValue(scope.url)) {
                        	
                        	urlValue = getHostServerFileAgilize() + scope.url.pathLogical;
                            setCss();
                        }
                    });
                    scope.$watch('css', function () {
                        if (!isNullValue(scope.css)) {
                            elm.addClass(scope.css);
                            setCss();
                        }
                    });
                });
            }
        };
    }
]);
