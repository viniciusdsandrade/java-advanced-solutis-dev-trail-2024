package br.com.agilizeware.geo.localization.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.geo.localization.MongoConfig;
import br.com.agilizeware.rest.ServiceRestAb;

@RestController
@RequestMapping("/locales")
@Service
public class LocalServiceRest extends ServiceRestAb {

    @Autowired
    private MongoConfig mongo;

    private static final Map<String, String> MESSAGES = new HashMap<>();

    static {
        LocalServiceRest.MESSAGES.put("invalid.id", "invalid.id");
        LocalServiceRest.MESSAGES.put("invalid.name", "invalid.name");
        LocalServiceRest.MESSAGES.put("invalid.type", "invalid.type");
        LocalServiceRest.MESSAGES.put("invalid.latitude", "invalid.latitude");
        LocalServiceRest.MESSAGES.put("invalid.longitude", "invalid.longitude");
        LocalServiceRest.MESSAGES.put("not.found", "not.found");
        LocalServiceRest.MESSAGES.put("internal.error", "internal.error");
    }

    private MongoCollection<Document> getCollection() throws Exception {
        MongoCollection<Document> locais = ((MongoClient) this.mongo.mongo()).getDatabase(this.mongo.getDatabaseName()).getCollection("locais");
        locais.createIndex(Indexes.geo2dsphere("address.location"));
        return locais;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public RestResultDto<Void> insert(@RequestBody Document record) throws JsonProcessingException, Exception {
        RestResultDto<Void> result = new RestResultDto<>();
        List<String> messages = new ArrayList<>();
        if (!record.containsKey("type") || record.get("type").toString().trim().isEmpty()) {
            messages.add(LocalServiceRest.MESSAGES.get("invalid.type"));
        }
        if (!record.containsKey("name") || record.get("name").toString().trim().isEmpty()) {
            messages.add(LocalServiceRest.MESSAGES.get("invalid.name"));
        }
        if (!record.containsKey("address") || !record.get("address", Map.class).containsKey("location")) {
            messages.add(LocalServiceRest.MESSAGES.get("invalid.latitude"));
            messages.add(LocalServiceRest.MESSAGES.get("invalid.longitude"));
        } else {
            try {
                Map location = Map.class.cast(record.get("address", Map.class).get("location"));
                if (!location.containsKey("latitude") || location.get("latitude").toString().trim().isEmpty()) {
                    messages.add(LocalServiceRest.MESSAGES.get("invalid.latitude"));
                }
                if (!location.containsKey("longitude") || location.get("longitude").toString().trim().isEmpty()) {
                    messages.add(LocalServiceRest.MESSAGES.get("invalid.longitude"));
                }
            } catch (ClassCastException e) {
                messages.add(LocalServiceRest.MESSAGES.get("invalid.latitude"));
                messages.add(LocalServiceRest.MESSAGES.get("invalid.longitude"));
            }
        }
        if (messages.isEmpty()) {
            record.get("address", Map.class).put("location", this.encodeLocation(Map.class.cast(record.get("address", Map.class).get("location"))));
            try {
                this.getCollection().insertOne(record);
                result.setSuccess(true);
            } catch (Exception e) {
                this.fillResultWithException(result, e);
            }
        } else {
            result.setSuccess(false);
            result.setStrAgilizeExceptionError(String.join(",", messages));
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public RestResultDto<Void> update(@RequestBody Document record) throws JsonProcessingException, Exception {
        RestResultDto<Void> result = new RestResultDto<>();
        List<String> messages = new ArrayList<>();
        if (!record.containsKey("id") || record.get("id").toString().trim().isEmpty()) {
            messages.add(LocalServiceRest.MESSAGES.get("invalid.id"));
        } else {
            record.put("_id", this.encodeId(record.getString("id")));
            record.remove("id");
        }
        if (!record.containsKey("type") || record.get("type").toString().trim().isEmpty()) {
            messages.add(LocalServiceRest.MESSAGES.get("invalid.type"));
        }
        if (!record.containsKey("name") || record.get("name").toString().trim().isEmpty()) {
            messages.add(LocalServiceRest.MESSAGES.get("invalid.name"));
        }
        if (!record.containsKey("address") || !record.get("address", Map.class).containsKey("location")) {
            messages.add(LocalServiceRest.MESSAGES.get("invalid.latitude"));
            messages.add(LocalServiceRest.MESSAGES.get("invalid.longitude"));
        } else {
            try {
                Map location = Map.class.cast(record.get("address", Map.class).get("location"));
                if (!location.containsKey("latitude") || location.get("latitude").toString().trim().isEmpty()) {
                    messages.add(LocalServiceRest.MESSAGES.get("invalid.latitude"));
                }
                if (!location.containsKey("longitude") || location.get("longitude").toString().trim().isEmpty()) {
                    messages.add(LocalServiceRest.MESSAGES.get("invalid.longitude"));
                }
            } catch (ClassCastException e) {
                messages.add(LocalServiceRest.MESSAGES.get("invalid.latitude"));
                messages.add(LocalServiceRest.MESSAGES.get("invalid.longitude"));
            }
        }
        if (messages.isEmpty()) {
            record.get("address", Map.class).put("location", this.encodeLocation(Map.class.cast(record.get("address", Map.class).get("location"))));
            try {
                if (!this.getCollection().findOneAndReplace(Filters.eq("_id", record.getObjectId("_id")), record).isEmpty()) {
                    result.setSuccess(true);
                } else {
                    result.setSuccess(false);
                    result.setStrAgilizeExceptionError(LocalServiceRest.MESSAGES.get("not.found"));
                }
            } catch (Exception e) {
                this.fillResultWithException(result, e);
            }
        } else {
            result.setSuccess(false);
            result.setStrAgilizeExceptionError(String.join(",", messages));
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    @ResponseBody
    public RestResultDto<Void> delete(@PathVariable(value = "id") String id) throws JsonProcessingException, Exception {
        RestResultDto<Void> result = new RestResultDto<>();
        if ((id == null) || id.trim().isEmpty()) {
            result.setSuccess(false);
            result.setStrAgilizeExceptionError(LocalServiceRest.MESSAGES.get("invalid.id"));
        } else {
            try {
                if (this.getCollection().findOneAndDelete(Filters.eq("_id", this.encodeId(id))) != null) {
                    result.setSuccess(true);
                } else {
                    result.setSuccess(false);
                    result.setStrAgilizeExceptionError(LocalServiceRest.MESSAGES.get("not.found"));
                }
            } catch (Exception e) {
                this.fillResultWithException(result, e);
            }
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    @ResponseBody
    public RestResultDto<Document> find(@PathVariable(value = "id") String id) throws JsonProcessingException, Exception {
        RestResultDto<Document> result = new RestResultDto<>();
        if ((id == null) || id.trim().isEmpty()) {
            result.setSuccess(false);
            result.setStrAgilizeExceptionError(LocalServiceRest.MESSAGES.get("invalid.id"));
        } else {
            try {
                MongoCursor<Document> rows = this.getCollection().find(Filters.eq("_id", this.encodeId(id))).iterator();
                if (rows.hasNext()) {
                    Document row = rows.next();
                    row.put("id", this.decodeId(row.getObjectId("_id")));
                    row.remove("_id");
                    row.get("address", Map.class).put("location", this.decodeLocation(Document.class.cast(row.get("address", Map.class).get("location"))));
                    result.setSuccess(true);
                    result.setData(row);
                } else {
                    result.setSuccess(false);
                    result.setStrAgilizeExceptionError(LocalServiceRest.MESSAGES.get("not.found"));
                }
            } catch (Exception e) {
                this.fillResultWithException(result, e);
            }
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public RestResultDto<List<Document>> search(@RequestParam(required = false) String type, @RequestParam(required = false) String name, @RequestParam(required = false) Double latitude, @RequestParam(required = false) Double longitude) throws JsonProcessingException, Exception {
        RestResultDto<List<Document>> result = new RestResultDto<>();
        try {
            Bson filter = null;
            if ((type != null) && !type.trim().isEmpty()) {
                filter = Filters.eq("type", type);
            }
            if ((name != null) && !name.trim().isEmpty()) {
                if (filter != null) {
                    filter = Filters.and(filter, Filters.regex("name", "[a-z]*(" + name + ")+[a-z]*"));
                } else {
                    filter = Filters.regex("name", "[a-z]*(" + name + ")+[a-z]*");
                }
            }
            if ((latitude != null) && (longitude != null)) {
                if (filter != null) {
                    filter = Filters.and(filter, Filters.nearSphere("address.location", new Point(new Position(longitude, latitude)), 100.0, 0.0));
                } else {
                    filter = Filters.nearSphere("address.location", new Point(new Position(longitude, latitude)), 100.0, 0.0);
                }
            }
            MongoCursor<Document> rows;
            if (filter != null) {
                rows = this.getCollection().find(filter).iterator();
            } else {
                rows = this.getCollection().find().iterator();
            }
            List<Document> list = new ArrayList<>();
            Document row;
            while (rows.hasNext()) {
                row = rows.next();
                row.put("id", this.decodeId(row.getObjectId("_id")));
                row.remove("_id");
                row.get("address", Map.class).put("location", this.decodeLocation(Document.class.cast(row.get("address", Map.class).get("location"))));
                list.add(row);
            }
            if (list.isEmpty()) {
                result.setSuccess(false);
                result.setStrAgilizeExceptionError(LocalServiceRest.MESSAGES.get("not.found"));
            } else {
                result.setSuccess(true);
                result.setData(list);
            }
        } catch (Exception e) {
            this.fillResultWithException(result, e);
        }
        return result;
    }

    private void fillResultWithException(RestResultDto result, Exception e) {
        e.printStackTrace();
        result.setSuccess(false);
        result.setStrAgilizeExceptionError(LocalServiceRest.MESSAGES.get("internal.error"));
    }

    private ObjectId encodeId(String id) {
        return new ObjectId(id);
    }

    private String decodeId(ObjectId id) {
        return id.toString();
    }

    private Point encodeLocation(Map location) {
        return new Point(new Position(Double.valueOf(location.get("longitude").toString()), Double.valueOf(location.get("latitude").toString())));
    }

    private Map decodeLocation(Document point) {
        Map location = new HashMap();
        location.put("longitude", point.get("coordinates", List.class).get(0));
        location.put("latitude", point.get("coordinates", List.class).get(1));
        return location;
    }
}
