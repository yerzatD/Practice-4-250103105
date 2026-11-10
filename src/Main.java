public class Main {
    public static void main(String[] args) {
        RealDocumentService real = new RealDocumentService();
        DocumentProxy proxy = new DocumentProxy(real);
        DocumentService service = proxy;

        request(service, real, 1, "D1", Role.STUDENT);
        request(service, real, 2, "D2", Role.STAFF);
        request(service, real, 3, "D2", Role.STUDENT);
        request(service, real, 4, "D2", Role.STAFF);
        request(service, real, 5, "D3", Role.STUDENT);

        proxy.clearCache();
        System.out.println("6. Cache cleared | load count = " + real.getLoadCount());

        request(service, real, 7, "D1", Role.STUDENT);
    }

    private static void request(DocumentService s, RealDocumentService real,
                                int step, String id, Role role) {
        String result;
        try {
            result = s.readDocument(id, role);
        } catch (SecurityException | IllegalArgumentException e) {
            result = e.getClass().getSimpleName();
        }
        System.out.println(step + ". id=" + id + " | role=" + role
                + " | result=" + result + " | load count = " + real.getLoadCount());
    }
}