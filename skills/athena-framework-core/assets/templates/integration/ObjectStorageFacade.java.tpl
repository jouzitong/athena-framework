package {{PACKAGE}}.storage;

import org.athena.framework.minio.model.PutObjectRequest;
import org.athena.framework.minio.model.StoredObject;
import org.athena.framework.minio.service.ObjectStorageService;
import org.springframework.stereotype.Service;

@Service
public class {{NAME}}ObjectStorageFacade {

    private final ObjectStorageService objectStorageService;

    public {{NAME}}ObjectStorageFacade(ObjectStorageService objectStorageService) {
        this.objectStorageService = objectStorageService;
    }

    public StoredObject put(String objectKey, String contentType, byte[] bytes) {
        PutObjectRequest request = new PutObjectRequest();
        request.setObjectKey(objectKey);
        request.setContentType(contentType);
        request.setBytes(bytes);
        request.setSize((long) bytes.length);
        return objectStorageService.putObject(request);
    }
}
