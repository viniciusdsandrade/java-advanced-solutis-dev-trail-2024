(function () {
    "use strict";
    angular.module("geoApp", [
        "geoHome", "geoLocal", "ngRoute"
    ]).config([
        "$routeProvider",
        function ($routeProvider) {
            $routeProvider.otherwise({
                redirectTo: "/home"
            });
        }
    ]).controller("AppController", [
        "$scope",
        function ($scope) {
            $scope.pages = {
                active: "home",
                getClass: function (page) {
                    if ($scope.pages.active === page) {
                        return "active";
                    } else {
                        return "";
                    }
                },
                closeMenu: function () {
                    if ($("#geo-menu-toggle").is(":visible")) {
                        $("#geo-menu").collapse("hide");
                    }
                }
            };
            $scope.message = {
                loading: function (show) {
                    $("#loading").css("display", show ? "block" : "none");
                },
                error: function (text) {
                    $("#messageErrorModalText").html(text);
                    $("#messageErrorModal").modal();
                },
                warning: function (text) {
                    $("#messageWarningModalText").html(text);
                    $("#messageWarningModal").modal();
                },
                success: function (text) {
                    $("#messageSuccessModalText").html(text);
                    $("#messageSuccessModal").modal();
                }
            };
        }
    ]).constant('DATABASE', {
        data: new Array()
    });
})();