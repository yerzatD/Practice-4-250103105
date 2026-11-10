import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DocumentProxy implements DocumentService {
    private static final Set<String> STAFF_ONLY = new HashSet<>(Arrays.asList("D2"));

    private final DocumentService service;
    private final Map<String, String> cache = new HashMap<>();

    public DocumentProxy(DocumentService service) {
        if (service == null) {
            throw new IllegalArgumentException("service must not be null");
        }
        this.service = service;
    }

    @Override
    public String readDocument(String id, Role role) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        if (role == null) {
            throw new IllegalArgumentException("role must not be null");
        }
        String key = id.trim();
        if (STAFF_ONLY.contains(key) && role != Role.STAFF) {
            throw new SecurityException("Access denied: " + role + " -> " + key);
        }
        String cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        String content = service.readDocument(key, role);
        cache.put(key, content);
        return content;
    }

    public void clearCache() {
        cache.clear();
    }

    public int cacheSize() {
        return cache.size();
    }
}