(function () {
    "use strict";
    angular.module("geoHome", [
        "ngRoute"
    ]).config([
        "$routeProvider",
        function ($routeProvider) {
            $routeProvider.when("/home", {
                templateUrl: "home/content.html",
                controller: "HomeController"
            });
        }
    ]).controller("HomeController", [
        "$scope", "$http",
        function ($scope, $http) {
            $scope.pages.active = "home";
            $scope.filter = {
                latitude: -23.574747764642623,
                longitude: -46.6378815472126
            };
            var map = new google.maps.Map(document.getElementById("mapAddress"), {
                zoom: 18,
                center: {
                    lat: $scope.filter.latitude,
                    lng: $scope.filter.longitude
                }
            });
            $scope.results = [];
            $scope.markers = [];
            $scope.circle = null;
            $scope.drawCircle = function () {
                if ($scope.circle) {
                    $scope.circle.setMap(null);
                    $scope.circle = null;
                }
                $scope.circle = new google.maps.Circle({
                    strokeColor: "#00FF00",
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: "#00FF00",
                    fillOpacity: 0.05,
                    map: map,
                    center: {
                        lat: $scope.filter.latitude,
                        lng: $scope.filter.longitude
                    },
                    radius: 100
                });
                $scope.circle.addListener("click", $scope.mapOnclick);
            };
            $scope.show = function () {
                $scope.message.loading(true);
                for (var i = 0; i < $scope.markers.length; i++) {
                    $scope.markers[i].setMap(null);
                }
                $scope.markers = [];
                $scope.result = [];
                $http.get("./locales?type=" + ($scope.filter.type ? $scope.filter.type : "") + "&latitude=" + $scope.filter.latitude + "&longitude=" + $scope.filter.longitude, {
                    headers: {
                        "X-Auth-Service": "23dKLiopXi89J6jhy2snY7Y7J9kas00mnI1l42ewTBVFIo091aLOo01o"
                    }
                }).then(function (result) {
                    if (result.data.success) {
                        $scope.result = result.data.data;
                        for (var i = 0; i < $scope.result.length; i++) {
                            $scope.markers[i] = new google.maps.Marker({
                                position: new google.maps.LatLng($scope.result[i].address.location.latitude, $scope.result[i].address.location.longitude),
                                map: map,
                                title: $scope.result[i].type + ": " + $scope.result[i].name
                            });
                        }
                    } else {
                        $scope.message.warning(result.data.strAgilizeExceptionError);
                    }
                    $scope.drawCircle();
                    $scope.message.loading(false);
                }, function () {
                    $scope.message.error("Ocorreu erro na requisição HTTP");
                    $scope.message.loading(false);
                });
                $scope.message.loading(false);
            };
            $scope.mapOnclick = function (event) {
                $scope.filter.latitude = event.latLng.lat();
                $scope.filter.longitude = event.latLng.lng();
                $scope.show();
            };
            map.addListener("click", $scope.mapOnclick);
            $scope.show();
        }
    ]);
})();