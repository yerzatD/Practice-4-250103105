public class ProxyTests {
    private static int passed = 0, failed = 0;

    private static void check(String name, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println((ok ? "PASS " : "FAIL ") + name);
    }

    private static Class<?> thrown(Runnable r) {
        try {
            r.run();
            return null;
        } catch (RuntimeException e) {
            return e.getClass();
        }
    }

    public static void main(String[] args) {
        // T1
        {
            RealDocumentService r = new RealDocumentService();
            DocumentProxy p = new DocumentProxy(r);
            String a = p.readDocument("D1", Role.STUDENT);
            String b = p.readDocument("D1", Role.STUDENT);
            check("T1 student reads D1 twice",
                    a.equals("Course timetable") && b.equals("Course timetable") && r.getLoadCount() == 1);
        }
        // T2
        {
            RealDocumentService r = new RealDocumentService();
            DocumentProxy p = new DocumentProxy(r);
            String a = p.readDocument("D1", Role.STUDENT);
            String b = p.readDocument("D1", Role.STAFF);
            check("T2 shared cache between roles",
                    a.equals(b) && a.equals("Course timetable") && r.getLoadCount() == 1);
        }
        // T3
        {
            RealDocumentService r = new RealDocumentService();
            DocumentProxy p = new DocumentProxy(r);
            Class<?> ex = thrown(() -> p.readDocument("D2", Role.STUDENT));
            check("T3 student denied D2, empty cache",
                    ex == SecurityException.class && r.getLoadCount() == 0 && p.cacheSize() == 0);
        }
        // T4
        {
            RealDocumentService r = new RealDocumentService();
            DocumentProxy p = new DocumentProxy(r);
            String a = p.readDocument("D2", Role.STAFF);
            Class<?> ex = thrown(() -> p.readDocument("D2", Role.STUDENT));
            String b = p.readDocument("D2", Role.STAFF);
            check("T4 permission checked before cache",
                    a.equals("Staff meeting notes") && b.equals("Staff meeting notes")
                            && ex == SecurityException.class && r.getLoadCount() == 1);
        }
        // T5
        {
            RealDocumentService r = new RealDocumentService();
            DocumentProxy p = new DocumentProxy(r);
            String a = p.readDocument("D1", Role.STUDENT);
            String b = p.readDocument("D3", Role.STUDENT);
            String c = p.readDocument("D1", Role.STUDENT);
            check("T5 distinct documents",
                    a.equals("Course timetable") && b.equals("Library guide")
                            && c.equals("Course timetable") && r.getLoadCount() == 2);
        }
        // T6
        {
            RealDocumentService r = new RealDocumentService();
            DocumentProxy p = new DocumentProxy(r);
            p.readDocument("D1", Role.STUDENT);
            int c1 = r.getLoadCount();
            p.clearCache();
            int c2 = r.getLoadCount();
            p.readDocument("D1", Role.STUDENT);
            int c3 = r.getLoadCount();
            check("T6 clearCache and reload", c1 == 1 && c2 == 1 && c3 == 2);
        }
        // T7
        {
            RealDocumentService r = new RealDocumentService();
            DocumentProxy p = new DocumentProxy(r);
            Class<?> e1 = thrown(() -> p.readDocument("DX", Role.STAFF));
            Class<?> e2 = thrown(() -> p.readDocument("DX", Role.STAFF));
            check("T7 unknown id twice",
                    e1 == IllegalArgumentException.class && e2 == IllegalArgumentException.class
                            && r.getLoadCount() == 2 && p.cacheSize() == 0);
        }
        // T8
        {
            RealDocumentService r = new RealDocumentService();
            DocumentProxy p = new DocumentProxy(r);
            Class<?> e1 = thrown(() -> p.readDocument(null, Role.STUDENT));
            Class<?> e2 = thrown(() -> p.readDocument("   ", Role.STUDENT));
            Class<?> e3 = thrown(() -> p.readDocument("D1", null));
            Class<?> e4 = thrown(() -> new DocumentProxy(null));
            Class<?> iae = IllegalArgumentException.class;
            check("T8 invalid arguments",
                    e1 == iae && e2 == iae && e3 == iae && e4 == iae
                            && r.getLoadCount() == 0 && p.cacheSize() == 0);
        }
        // T9
        {
            RealDocumentService r = new RealDocumentService();
            DocumentProxy p = new DocumentProxy(r);
            String a = p.readDocument(" D1 ", Role.STUDENT);
            String b = p.readDocument("D1", Role.STUDENT);
            check("T9 trimmed id",
                    a.equals("Course timetable") && b.equals("Course timetable")
                            && p.cacheSize() == 1 && r.getLoadCount() == 1);
        }

        System.out.println("Summary: " + passed + " passed, " + failed + " failed");
    }
}