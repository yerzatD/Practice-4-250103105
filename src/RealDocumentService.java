import java.util.HashMap;
import java.util.Map;

public class RealDocumentService implements DocumentService {
    private final Map<String, String> storage = new HashMap<>();
    private int loadCount = 0;

    public RealDocumentService() {
        storage.put("D1", "Course timetable");
        storage.put("D2", "Staff meeting notes");
        storage.put("D3", "Library guide");
    }

    @Override
    public String readDocument(String id, Role role) {
        loadCount++;
        String content = storage.get(id);
        if (content == null) {
            throw new IllegalArgumentException("Unknown document: " + id);
        }
        return content;
    }

    public int getLoadCount() {
        return loadCount;
    }
}