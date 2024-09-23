(function () {
    "use strict";
    angular.module("geoLocal", [
        "ngRoute"
    ]).config([
        "$routeProvider",
        function ($routeProvider) {
            $routeProvider.when("/locais", {
                templateUrl: "local/list.html",
                controller: "LocaisController"
            }).when("/local/:id", {
                templateUrl: "local/content.html",
                controller: "LocalController"
            });
        }
    ]).controller("LocaisController", [
        "$scope", "$location", "$http",
        function ($scope, $location, $http) {
            $scope.pages.active = "local";
            $scope.filter = {};
            $scope.result = [];
            $scope.search = function () {
                $scope.result = [];
                $http.get("./locales?type=" + ($scope.filter.type ? $scope.filter.type : "") + "&name=" + ($scope.filter.name ? $scope.filter.name : ""), {headers: {"X-Auth-Service": "23dKLiopXi89J6jhy2snY7Y7J9kas00mnI1l42ewTBVFIo091aLOo01o"}}).then(function (result) {
                    if (result.data.success) {
                        $scope.result = result.data.data;
                    } else {
                        $scope.message.warning(result.data.strAgilizeExceptionError);
                    }
                    $scope.message.loading(false);
                    return;
                }, function () {
                    $scope.message.error("Ocorreu erro na requisição HTTP");
                    $scope.message.loading(false);
                    return;
                });
            };
            $scope.open = function (id) {
                $location.path("/local/" + id);
            };
        }
    ]).controller("LocalController", [
        "$scope", "$routeParams", "$location", "$http", "LocalService",
        function ($scope, $routeParams, $location, $http, LocalService) {
            $scope.pages.active = "local";
            $scope.data = {};
            var map = new google.maps.Map(document.getElementById("mapAddress"), {
                zoom: 20,
                center: {
                    lat: -23.574747764642623,
                    lng: -46.6378815472126
                }
            });
            var marker = undefined;
            if ($routeParams.id !== "new") {
                $http.get("./locales/" + $routeParams.id, {headers: {"X-Auth-Service": "23dKLiopXi89J6jhy2snY7Y7J9kas00mnI1l42ewTBVFIo091aLOo01o"}}).then(function (result) {
                    if (result.data.success) {
                        $scope.data = result.data.data;
                        if ($scope.data && $scope.data.address && $scope.data.address.location && $scope.data.address.location.latitude && $scope.data.address.location.longitude) {
                            if (marker) {
                                marker.setMap(null);
                            }
                            marker = new google.maps.Marker({
                                position: {
                                    lat: $scope.data.address.location.latitude,
                                    lng: $scope.data.address.location.longitude
                                },
                                map: map,
                                title: 'Local selecionado!'
                            });
                            map.setCenter({
                                lat: $scope.data.address.location.latitude,
                                lng: $scope.data.address.location.longitude
                            });
                        }
                    } else {
                        $location.path("/locais");
                        $scope.message.error(result.data.strAgilizeExceptionError);
                    }
                    $scope.message.loading(false);
                    return;
                }, function () {
                    $location.path("/locais");
                    $scope.message.error("Ocorreu erro na requisição HTTP");
                    $scope.message.loading(false);
                    return;
                });
            }
            map.addListener("click", function (event) {
                $scope.message.loading(true);
                LocalService.findByLatLng(event.latLng.lat(), event.latLng.lng(), function (address) {
                    if (address.found) {
                        $scope.data.address = address;
                        if (marker) {
                            marker.setMap(null);
                        }
                        marker = new google.maps.Marker({
                            position: event.latLng,
                            map: map,
                            title: 'Local selecionado!'
                        });
                        map.setCenter(event.latLng);
                    }
                    $scope.message.loading(false);
                });
            });
            $scope.getLatLng = function () {
                if ($scope.data.address && $scope.data.address.country && $scope.data.address.state && $scope.data.address.city && $scope.data.address.district && $scope.data.address.street && $scope.data.address.number && $scope.data.address.postalCode) {
                    $scope.message.loading(true);
                    LocalService.findByAddress($scope.data.address, function (found, latitude, longitude) {
                        if (found) {
                            $scope.data.address.location.latitude = latitude;
                            $scope.data.address.location.longitude = longitude;
                            if (marker) {
                                marker.setMap(null);
                            }
                            marker = new google.maps.Marker({
                                position: new google.maps.LatLng(latitude, longitude),
                                map: map,
                                title: 'Local selecionado!'
                            });
                            map.setCenter(new google.maps.LatLng(latitude, longitude));
                        }
                        $scope.message.loading(false);
                    });
                } else {
                    $scope.message.warning("Defina todos os campos do endereço para poder obter latitude e longitude");
                }
            };
            $scope.save = function () {
                if ($scope.data && $scope.data.type && $scope.data.name && $scope.data.description && $scope.data.address && $scope.data.address.country && $scope.data.address.state && $scope.data.address.city && $scope.data.address.district && $scope.data.address.street && $scope.data.address.number && $scope.data.address.postalCode) {
                    $scope.message.loading(true);
                    delete $scope.data.address.found;
                    if ($scope.data.id) {
                        $http.put("./locales", JSON.stringify($scope.data), {headers: {"X-Auth-Service": "23dKLiopXi89J6jhy2snY7Y7J9kas00mnI1l42ewTBVFIo091aLOo01o"}}).then(function (result) {
                            if (result.data.success) {
                                $location.path("/locais");
                                $scope.message.success("Registro alterado!");
                            } else {
                                $scope.message.error(result.data.strAgilizeExceptionError);
                            }
                            $scope.message.loading(false);
                            return;
                        }, function () {
                            $scope.message.error("Ocorreu erro na requisição HTTP");
                            $scope.message.loading(false);
                            return;
                        });
                    } else {
                        $http.post("./locales", JSON.stringify($scope.data), {headers: {"X-Auth-Service": "23dKLiopXi89J6jhy2snY7Y7J9kas00mnI1l42ewTBVFIo091aLOo01o"}}).then(function (result) {
                            if (result.data.success) {
                                $location.path("/locais");
                                $scope.message.success("Registro inserido!");
                            } else {
                                $scope.message.error(result.data.strAgilizeExceptionError);
                            }
                            $scope.message.loading(false);
                            return;
                        }, function () {
                            $scope.message.error("Ocorreu erro na requisição HTTP");
                            $scope.message.loading(false);
                            return;
                        });
                    }
                } else {
                    $scope.message.warning("Defina todos os campos para salvar o local");
                }
            };
            $scope.delete = function () {
                $http.delete("./locales/" + $scope.data.id, {headers: {"X-Auth-Service": "23dKLiopXi89J6jhy2snY7Y7J9kas00mnI1l42ewTBVFIo091aLOo01o"}}).then(function (result) {
                    if (result.data.success) {
                        $location.path("/locais");
                        $scope.message.success("Registro apagado!");
                    } else {
                        $scope.message.error(result.data.strAgilizeExceptionError);
                    }
                    $scope.message.loading(false);
                    return;
                }, function () {
                    $scope.message.error("Ocorreu erro na requisição HTTP");
                    $scope.message.loading(false);
                    return;
                });
            };
            $scope.cancel = function () {
                $location.path("/locais");
            };
        }
    ]).factory('LocalService', [
        "$http",
        function ($http) {
            var service = {
                findByLatLng: function (latitude, longitude, apply) {
                    $http.get("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true").then(function (result) {
                        var data = undefined;
                        for (var i = 0; i < result.data.results.length; i++) {
                            if (result.data.results[i].types.indexOf("street_address") !== -1) {
                                data = result.data.results[i].address_components;
                                break;
                            }
                        }
                        if (data) {
                            var address = {
                                found: true,
                                location: {
                                    latitude: latitude,
                                    longitude: longitude
                                }
                            };
                            for (var i = 0; i < data.length; i++) {
                                if (data[i].types.indexOf("street_number") !== -1) {
                                    address.number = data[i].long_name;
                                } else if (data[i].types.indexOf("route") !== -1) {
                                    address.street = data[i].long_name;
                                } else if ((data[i].types.indexOf("political") !== -1) && (data[i].types.indexOf("sublocality") !== -1) && (data[i].types.indexOf("sublocality_level_1") !== -1)) {
                                    address.district = data[i].long_name;
                                } else if ((data[i].types.indexOf("political") !== -1) && (data[i].types.indexOf("locality") !== -1)) {
                                    address.city = data[i].long_name;
                                } else if ((data[i].types.indexOf("political") !== -1) && (data[i].types.indexOf("administrative_area_level_1") !== -1)) {
                                    address.state = data[i].long_name;
                                } else if ((data[i].types.indexOf("political") !== -1) && (data[i].types.indexOf("country") !== -1)) {
                                    address.country = data[i].long_name;
                                } else if ((data[i].types.indexOf("postal_code") !== -1)) {
                                    address.postalCode = data[i].long_name;
                                }
                            }
                            apply(address);
                        } else {
                            apply({
                                found: false
                            });
                        }
                    }, function () {
                        apply({
                            found: false
                        });
                    });
                },
                findByAddress: function (address, apply) {
                    new google.maps.Geocoder().geocode({address: address.street + ", " + address.number + ", " + address.district + ", " + address.city + ", " + address.state + ", " + address.country + ", " + address.postalCode}, function (results, status) {
                        if ((status === google.maps.GeocoderStatus.OK) && (status !== google.maps.GeocoderStatus.ZERO_RESULTS)) {
                            apply(true, results[0].geometry.location.lat(), results[0].geometry.location.lng());
                        } else {
                            apply(false);
                        }
                    });
                }
            };
            return service;
        }
    ]);
})();