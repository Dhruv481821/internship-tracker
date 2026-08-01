import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        Application test = new Application(0, "Google", "SDE Intern", "Applied", "2026-08-01", null, "Applied via campus placement drive");
        int newId = Database.insert(test);
        System.out.println("Inserted row with id: " + newId);

        List<Application> all = Database.getAll();
        System.out.println("Total applications: " + all.size());
        for (Application a : all) {
            System.out.println(a);
        }
    }
}