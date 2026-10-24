package com.isha.aiinterview.cache;

import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Master question bank holding all 200+ pre-loaded questions organized by topic.
 * Used as a fallback when Gemini API is unavailable or rate-limited.
 */
@Service
public class QuestionBankService {

    // ── All topic banks ───────────────────────────────────────────────────────
    private final Map<String, List<CachedQuestion>> BANK = new LinkedHashMap<>();

    public QuestionBankService() {
        BANK.put("HR & Introduction",          buildHrQuestions());
        BANK.put("Java Core",                  buildJavaCoreQuestions());
        BANK.put("Java OOP",                   buildJavaOopQuestions());
        BANK.put("Java Collections & Exceptions", buildJavaCollectionsQuestions());
        BANK.put("SQL & DBMS",                 buildSqlDbmsQuestions());
        BANK.put("SQL Queries",                buildSqlQueryQuestions());
        BANK.put("Spring Boot",                buildSpringBootQuestions());
        BANK.put("REST API & HTTP",            buildRestApiQuestions());
        BANK.put("DSA - Arrays & Strings",     buildDsaArraysQuestions());
        BANK.put("DSA - Searching & Sorting",  buildDsaSearchSortQuestions());
        BANK.put("DSA - Data Structures",      buildDsaStructuresQuestions());
        BANK.put("Project - Placement Tracker",buildPlacementTrackerQuestions());
        BANK.put("Project - AI Generator",     buildAiGeneratorQuestions());
        BANK.put("Software Engineering",       buildSdlcQuestions());
        BANK.put("Computer Networks",          buildNetworksQuestions());
        BANK.put("Operating Systems",          buildOsQuestions());
        BANK.put("Cloud & Security",           buildCloudQuestions());
        BANK.put("Coding Problems",            buildCodingQuestions());
    }

    /**
     * Returns all topic names available in the bank.
     */
    public Set<String> getAllTopics() {
        return BANK.keySet();
    }

    /**
     * Returns all questions for a given topic name (case-insensitive partial match).
     */
    public List<CachedQuestion> getByTopic(String topic) {
        for (Map.Entry<String, List<CachedQuestion>> entry : BANK.entrySet()) {
            if (entry.getKey().toLowerCase().contains(topic.toLowerCase())) {
                return entry.getValue();
            }
        }
        return Collections.emptyList();
    }

    /**
     * Builds a realistic 5-round interview simulation (5 questions per set, 25 total)
     * ordered exactly like real 3-4 LPA freshers/campus interviews:
     * 1. Round 1: Introduction & Basic HR
     * 2. Round 2: Core Subject - Java & OOP Fundamentals
     * 3. Round 3: Core Subject - DBMS & SQL Fundamentals
     * 4. Round 4: Data Structures & Problem Solving (Basic DSA)
     * 5. Round 5: Resume Project Discussion & Spring Boot Architecture
     */
    public List<CachedCategory> selectRealInterviewSets(String jobTitle, String type) {
        List<CachedCategory> sets = new ArrayList<>();

        // ── Round 1: Introduction & Basic HR (5 Questions) ──────────────────────
        List<CachedQuestion> hrList = BANK.getOrDefault("HR & Introduction", Collections.emptyList());
        sets.add(new CachedCategory("Round 1: Introduction & HR Fundamentals", pickSlice(hrList, 0, 5)));

        // ── Round 2: Core Subject - Java & OOPs (5 Questions) ───────────────────
        List<CachedQuestion> oopList = BANK.getOrDefault("Java OOP", Collections.emptyList());
        List<CachedQuestion> coreList = BANK.getOrDefault("Java Core", Collections.emptyList());
        List<CachedQuestion> round2 = new ArrayList<>();
        if (!oopList.isEmpty()) round2.add(oopList.get(0)); // 4 pillars
        if (coreList.size() > 3) round2.add(coreList.get(3)); // == vs .equals
        if (coreList.size() > 5) round2.add(coreList.get(5)); // String immutability
        if (oopList.size() > 5) round2.add(oopList.get(5)); // interface vs abstract class
        List<CachedQuestion> exList = BANK.getOrDefault("Java Collections & Exceptions", Collections.emptyList());
        if (!exList.isEmpty()) round2.add(exList.get(0)); // Exception handling
        renumber(round2);
        sets.add(new CachedCategory("Round 2: Core Programming & OOP Fundamentals", round2));

        // ── Round 3: Core Subject - DBMS & SQL (5 Questions) ────────────────────
        List<CachedQuestion> dbmsList = BANK.getOrDefault("SQL & DBMS", Collections.emptyList());
        List<CachedQuestion> sqlList  = BANK.getOrDefault("SQL Queries", Collections.emptyList());
        List<CachedQuestion> round3 = new ArrayList<>();
        if (dbmsList.size() > 2) round3.add(dbmsList.get(2)); // ACID properties
        if (dbmsList.size() > 3) round3.add(dbmsList.get(3)); // Primary vs Foreign vs Unique
        if (dbmsList.size() > 1) round3.add(dbmsList.get(1)); // Normalization 1NF-3NF
        if (!sqlList.isEmpty())  round3.add(sqlList.get(0));  // Second highest salary query
        if (dbmsList.size() > 7) round3.add(dbmsList.get(7)); // DELETE vs TRUNCATE vs DROP
        renumber(round3);
        sets.add(new CachedCategory("Round 3: DBMS & SQL Fundamentals", round3));

        // ── Round 4: Data Structures & Algorithms (5 Questions) ─────────────────
        List<CachedQuestion> dsaStruct = BANK.getOrDefault("DSA - Data Structures", Collections.emptyList());
        List<CachedQuestion> dsaArrays = BANK.getOrDefault("DSA - Arrays & Strings", Collections.emptyList());
        List<CachedQuestion> round4 = new ArrayList<>();
        if (!dsaStruct.isEmpty()) round4.add(dsaStruct.get(0)); // Array vs LinkedList
        if (dsaArrays.size() > 1) round4.add(dsaArrays.get(1)); // Second largest element in O(n)
        if (dsaArrays.size() > 5) round4.add(dsaArrays.get(5)); // Palindrome check two pointer
        if (dsaStruct.size() > 3) round4.add(dsaStruct.get(3)); // Stack vs Queue / HashMap internal
        if (dsaArrays.size() > 2) round4.add(dsaArrays.get(2)); // Kadane's algorithm max subarray
        renumber(round4);
        sets.add(new CachedCategory("Round 4: Data Structures & Problem Solving (Basic DSA)", round4));

        // ── Round 5: Resume Project Discussion & Spring Boot (5 Questions) ───────
        List<CachedQuestion> projectList = BANK.getOrDefault("Project - Placement Tracker", Collections.emptyList());
        List<CachedQuestion> springList  = BANK.getOrDefault("Spring Boot", Collections.emptyList());
        List<CachedQuestion> restList    = BANK.getOrDefault("REST API & HTTP", Collections.emptyList());
        List<CachedQuestion> round5 = new ArrayList<>();
        if (!projectList.isEmpty()) round5.add(projectList.get(0)); // Explain project
        if (!springList.isEmpty())  round5.add(springList.get(0));  // Spring Boot vs Spring
        if (restList.size() > 2)    round5.add(restList.get(2));    // GET/POST/PUT/PATCH/DELETE
        if (restList.size() > 7)    round5.add(restList.get(7));    // Request validation & exception handling
        if (projectList.size() > 4) round5.add(projectList.get(4)); // Challenges / duplicate handling / bug
        renumber(round5);
        sets.add(new CachedCategory("Round 5: Resume Project & Backend Architecture", round5));

        return sets;
    }

    private List<CachedQuestion> pickSlice(List<CachedQuestion> list, int start, int count) {
        List<CachedQuestion> res = new ArrayList<>();
        if (list == null || list.isEmpty()) return res;
        for (int i = start; i < Math.min(start + count, list.size()); i++) {
            CachedQuestion q = list.get(i);
            res.add(new CachedQuestion(res.size() + 1, q.getQuestion(), q.getAnswer()));
        }
        return res;
    }

    private void renumber(List<CachedQuestion> list) {
        for (int i = 0; i < list.size(); i++) {
            CachedQuestion q = list.get(i);
            list.set(i, new CachedQuestion(i + 1, q.getQuestion(), q.getAnswer()));
        }
    }

    /**
     * Smart selection: returns 5-question rounds tailored for 3-4 LPA interview simulation.
     */
    public List<CachedCategory> selectCategories(String jobTitle, String type, int maxCategories) {
        return selectRealInterviewSets(jobTitle, type);
    }

    // ── Topic resolution logic ────────────────────────────────────────────────

    private List<String> resolveTopicKeys(String jobTitle, String type) {
        String jt = jobTitle.toLowerCase();
        List<String> keys = new ArrayList<>();

        boolean isTech       = type.equalsIgnoreCase("technical") || type.equalsIgnoreCase("mixed");
        boolean isBehavioral = type.equalsIgnoreCase("behavioral") || type.equalsIgnoreCase("mixed");

        // Always add HR for opening
        keys.add("HR & Introduction");

        // Java / Spring Boot roles
        if (jt.contains("java") || jt.contains("spring") || jt.contains("backend") || jt.contains("software")) {
            if (isTech) {
                keys.add("Java Core");
                keys.add("Java OOP");
                keys.add("Java Collections & Exceptions");
                keys.add("Spring Boot");
                keys.add("REST API & HTTP");
            }
        }

        // Data / SQL roles
        if (jt.contains("data") || jt.contains("sql") || jt.contains("database") || jt.contains("analyst")) {
            if (isTech) {
                keys.add("SQL & DBMS");
                keys.add("SQL Queries");
            }
        }

        // DSA / algorithm / general SWE
        if (jt.contains("engineer") || jt.contains("developer") || jt.contains("programmer")
                || jt.contains("dsa") || jt.contains("algorithm")) {
            if (isTech) {
                keys.add("DSA - Arrays & Strings");
                keys.add("DSA - Searching & Sorting");
                keys.add("DSA - Data Structures");
            }
        }

        // Cloud / DevOps
        if (jt.contains("cloud") || jt.contains("devops") || jt.contains("aws") || jt.contains("infra")) {
            if (isTech) keys.add("Cloud & Security");
        }

        // Network / OS
        if (jt.contains("network") || jt.contains("system") || jt.contains("os")) {
            if (isTech) {
                keys.add("Computer Networks");
                keys.add("Operating Systems");
            }
        }

        // Project-based questions for known projects
        if (jt.contains("placement") || jt.contains("tracker") || jt.contains("ltm")) {
            keys.add("Project - Placement Tracker");
        }
        if (jt.contains("ai") || jt.contains("ml") || jt.contains("gemini") || jt.contains("llm")) {
            keys.add("Project - AI Generator");
        }

        // Behavioral / mixed always gets HR, SE, soft-skills
        if (isBehavioral) {
            keys.add("Software Engineering");
        }

        // Default fallback: if no specific match, use general SWE stack
        if (keys.size() <= 1) {
            keys.addAll(Arrays.asList(
                    "Java Core", "Java OOP", "Spring Boot",
                    "DSA - Arrays & Strings", "Software Engineering"));
        }

        return keys;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ── Question Definitions ──────────────────────────────────────────────────
    // ─────────────────────────────────────────────────────────────────────────

    private List<CachedQuestion> buildHrQuestions() {
        return List.of(
            q(1,  "Tell me about yourself.",
               "I'm a Computer Science Engineering student with hands-on experience in Java and Spring Boot. I've built two projects — a Student Placement Tracker and an AI Interview Question Generator using the Gemini API. I'm passionate about backend development and enjoy solving real-world problems through clean, scalable code."),
            q(2,  "Walk me through your resume.",
               "My resume highlights my educational background in CSE, two full-stack Java projects, certifications in Java (NPTEL) and Cloud Computing, and participation in GirlScript Summer of Code. Each section reflects skills I've actively applied, not just studied."),
            q(3,  "Why did you choose Computer Science and Engineering?",
               "I've always been drawn to problem-solving. CSE gave me a structured way to combine logical thinking with real-world impact — building systems that people actually use."),
            q(4,  "Why did you choose Java as your primary programming language?",
               "Java is strongly typed, platform-independent, and has an enormous ecosystem — especially for enterprise backend development. It enforces good OOP practices which make code maintainable at scale."),
            q(5,  "Why did you choose Spring Boot?",
               "Spring Boot eliminates boilerplate configuration. With auto-configuration, embedded servers, and Spring Data JPA, I could focus on business logic instead of infrastructure setup."),
            q(6,  "What are your strengths and weaknesses?",
               "Strength: I break down complex problems methodically and write clean, documented code. Weakness: I sometimes over-engineer simple solutions — I've been working on recognizing when 'good enough' is the right call."),
            q(7,  "Why should we hire you?",
               "I bring practical project experience, a solid CS foundation, and a genuine eagerness to learn. I don't just know concepts — I've implemented them in working applications."),
            q(8,  "What are your short-term and long-term career goals?",
               "Short-term: grow as a backend Java developer and master system design. Long-term: lead engineering teams building scalable, impactful products."),
            q(9,  "How do you approach learning a new technology?",
               "I start with official documentation to understand the 'why', build a small proof-of-concept project, then apply it to something real. Hands-on practice beats passive reading every time."),
            q(10, "What would you do if you were assigned a technology you had never used before?",
               "I'd read the official docs, follow a quick-start guide, find a community resource or tutorial, and ask teammates for guidance. I'm comfortable with uncertainty — learning new tools is part of the job."),
            q(11, "Tell me about a technical challenge you faced and how you solved it.",
               "While building the AI Interview Generator, Gemini API would occasionally return JSON with markdown fences instead of pure JSON. I added a regex-based stripping step in the parser and implemented a cached question fallback so users were never blocked."),
            q(12, "Are you comfortable working in a team and learning new technologies?",
               "Absolutely. GirlScript Summer of Code taught me collaborative development — code reviews, pull requests, and communicating technical decisions clearly. I enjoy both solo deep-work and team collaboration.")
        );
    }

    private List<CachedQuestion> buildJavaCoreQuestions() {
        return List.of(
            q(1,  "What is Java, and what are its main features?",
               "Java is a high-level, class-based, object-oriented language. Key features: platform independence (Write Once Run Anywhere), strong typing, automatic garbage collection, multithreading support, and a rich standard library."),
            q(2,  "Why is Java platform-independent?",
               "Java source code compiles to bytecode (.class files) rather than native machine code. The JVM on any OS interprets and executes that bytecode, making the same .jar run on Windows, Linux, or macOS without recompilation."),
            q(3,  "What is the difference between JDK, JRE, and JVM?",
               "JVM (Java Virtual Machine) executes bytecode. JRE = JVM + standard libraries needed to run Java apps. JDK = JRE + compiler (javac) + development tools. Developers need JDK; end-users only need JRE."),
            q(4,  "What is the difference between == and .equals() in Java?",
               "== compares object references (memory addresses) for objects, and values for primitives. .equals() compares the logical content of objects. For example, two different String objects with the same text are == false but .equals() true."),
            q(5,  "What is the difference between String, StringBuilder, and StringBuffer?",
               "String is immutable — every modification creates a new object. StringBuilder is mutable and not thread-safe, making it faster for single-threaded string manipulation. StringBuffer is mutable and thread-safe (synchronized), used in multi-threaded contexts."),
            q(6,  "Why are strings immutable in Java?",
               "Immutability enables string pool caching (saving memory), guarantees thread safety without synchronization, and makes strings safe as HashMap keys since their hashCode never changes after creation."),
            q(7,  "What are constructors? What are their types?",
               "A constructor is a special method called when an object is created. Types: (1) Default constructor — no parameters, provided by compiler if none defined. (2) Parameterized constructor — takes arguments. (3) Copy constructor — takes another object of the same class."),
            q(8,  "What is the static keyword?",
               "static belongs to the class rather than any instance. Static variables are shared across all objects. Static methods can be called without creating an instance. Static blocks run once when the class is loaded."),
            q(9,  "What is the final keyword?",
               "final on a variable makes it a constant (cannot be reassigned). final on a method prevents overriding. final on a class prevents inheritance. Used for safety, immutability, and optimization."),
            q(10, "What is the difference between method overloading and method overriding?",
               "Overloading: same method name, different parameter signatures, resolved at compile-time (static polymorphism). Overriding: subclass redefines a superclass method with the same signature, resolved at runtime (dynamic polymorphism)."),
            q(11, "What is garbage collection in Java?",
               "Garbage collection is the automatic process of reclaiming memory from objects no longer reachable by any live reference. The JVM's GC (e.g., G1, ZGC) periodically identifies and removes such objects, preventing memory leaks."),
            q(12, "What is the difference between pass-by-value and pass-by-reference?",
               "Java is strictly pass-by-value. For primitives, the value is copied. For objects, the reference (memory address) is copied — so the method can mutate the object's fields but cannot reassign the caller's variable to a new object.")
        );
    }

    private List<CachedQuestion> buildJavaOopQuestions() {
        return List.of(
            q(1,  "Explain the four pillars of OOP.",
               "Encapsulation: bundling data and methods, hiding internals. Inheritance: a class acquiring properties of another. Polymorphism: same interface, multiple behaviors. Abstraction: exposing only essential details, hiding implementation."),
            q(2,  "What is encapsulation? Give a practical example.",
               "Encapsulation hides internal state and requires interaction through public methods. Example: a BankAccount class keeps 'balance' private and exposes deposit()/withdraw() methods that validate before modifying the balance."),
            q(3,  "What is inheritance? What are its types in Java?",
               "Inheritance allows a subclass to reuse fields and methods of a superclass. Types in Java: Single, Multilevel, Hierarchical. Java does NOT support multiple class inheritance (to avoid the diamond problem) but supports it through interfaces."),
            q(4,  "What is polymorphism?",
               "Polymorphism means 'many forms'. Compile-time polymorphism = method overloading. Runtime polymorphism = method overriding via dynamic dispatch. Example: Animal.makeSound() behaves differently for Dog and Cat at runtime."),
            q(5,  "What is abstraction?",
               "Abstraction hides implementation complexity and exposes only the necessary interface. In Java, achieved via abstract classes (partial implementation) and interfaces (pure contract). Example: a List interface hides whether the backing store is an array or linked list."),
            q(6,  "What is the difference between an interface and an abstract class?",
               "Abstract class: can have state (fields), constructors, and concrete methods; supports single inheritance. Interface: no state (only constants), no constructors, all methods abstract by default (Java 8+ allows default/static methods); a class can implement multiple interfaces."),
            q(7,  "Does Java support multiple inheritance?",
               "Not for classes — avoided to prevent the diamond problem. Java does support multiple inheritance through interfaces: a class can implement multiple interfaces. If two interfaces have conflicting default methods, the implementing class must override to resolve."),
            q(8,  "What is the difference between compile-time and runtime polymorphism?",
               "Compile-time (static) polymorphism is resolved by the compiler based on method signatures — achieved via overloading. Runtime (dynamic) polymorphism is resolved during execution based on the actual object type — achieved via overriding with the @Override annotation."),
            q(9,  "What is the difference between an abstract class and a concrete class?",
               "An abstract class cannot be instantiated and may have abstract (unimplemented) methods that subclasses must implement. A concrete class provides full implementations of all its methods and can be instantiated directly with 'new'.")
        );
    }

    private List<CachedQuestion> buildJavaCollectionsQuestions() {
        return List.of(
            q(1,  "What is exception handling?",
               "Exception handling is a mechanism to handle runtime errors gracefully using try-catch-finally blocks. It separates error-handling logic from normal code flow, preventing abrupt application crashes."),
            q(2,  "What is the difference between checked and unchecked exceptions?",
               "Checked exceptions (e.g., IOException, SQLException) must be declared or caught at compile-time. Unchecked exceptions (extend RuntimeException, e.g., NullPointerException, ArrayIndexOutOfBoundsException) are not enforced by the compiler."),
            q(3,  "Explain try, catch, finally, throw, and throws.",
               "try: block where exception may occur. catch: handles a specific exception type. finally: always executes (for cleanup). throw: manually throws an exception. throws: declares in method signature that a method may throw a checked exception."),
            q(4,  "What is the Java Collections Framework?",
               "A unified architecture of interfaces and classes for storing and manipulating groups of objects. Key interfaces: Collection, List, Set, Map, Queue. Implementations include ArrayList, LinkedList, HashSet, HashMap, TreeMap, PriorityQueue."),
            q(5,  "What is the difference between List, Set, and Map?",
               "List: ordered, allows duplicates (ArrayList, LinkedList). Set: unordered (or sorted in TreeSet), no duplicates (HashSet, TreeSet). Map: key-value pairs, no duplicate keys (HashMap, TreeMap). Map does not extend Collection."),
            q(6,  "What is a HashMap, and how does it work?",
               "HashMap stores key-value pairs. Internally it uses an array of buckets. On put(key, value): key.hashCode() determines the bucket index; if a collision occurs, entries form a linked list (or tree for Java 8+ with >8 entries). Average O(1) get/put."),
            q(7,  "What happens when two keys have the same hash value?",
               "This is a hash collision. Both entries go into the same bucket and are stored in a linked list (or a balanced tree in Java 8+ when the list exceeds 8 entries). Retrieval uses .equals() to find the exact key within that bucket."),
            q(8,  "What is the difference between HashMap and Hashtable?",
               "HashMap: not synchronized, allows one null key and multiple null values, faster. Hashtable: synchronized (thread-safe), does not allow null keys or values, legacy class. Use ConcurrentHashMap instead of Hashtable for thread-safe modern code."),
            q(9,  "What is the difference between ArrayList and LinkedList?",
               "ArrayList: backed by a dynamic array; O(1) random access; O(n) insert/delete in the middle. LinkedList: doubly-linked nodes; O(n) random access; O(1) insert/delete at head/tail. Use ArrayList for frequent reads, LinkedList for frequent insertions/deletions.")
        );
    }

    private List<CachedQuestion> buildSqlDbmsQuestions() {
        return List.of(
            q(1,  "What is the difference between DBMS and RDBMS?",
               "DBMS stores data without enforcing relationships. RDBMS stores data in structured tables with enforced relationships via foreign keys and supports SQL. Examples: DBMS = file system; RDBMS = MySQL, PostgreSQL, Oracle."),
            q(2,  "What is normalization? Explain 1NF, 2NF, and 3NF.",
               "Normalization eliminates redundancy and ensures data integrity. 1NF: atomic values, no repeating groups. 2NF: 1NF + no partial dependency on a composite key. 3NF: 2NF + no transitive dependency (non-key column depending on another non-key column)."),
            q(3,  "Explain the ACID properties.",
               "Atomicity: transaction is all-or-nothing. Consistency: DB moves from one valid state to another. Isolation: concurrent transactions don't interfere. Durability: committed data persists even after system failure."),
            q(4,  "What is the difference between a primary key and a foreign key?",
               "Primary key: uniquely identifies each row in a table; cannot be NULL. Foreign key: a column in one table that references the primary key of another table, enforcing referential integrity between related tables."),
            q(5,  "What is indexing in DBMS?",
               "Indexing creates a data structure (B-tree or hash) on a column to speed up search queries. Without an index, a full table scan is needed (O(n)). With an index, queries can run in O(log n). Trade-off: indexes use extra storage and slow down INSERT/UPDATE/DELETE."),
            q(6,  "What is the difference between clustered and non-clustered indexes?",
               "Clustered index determines the physical order of rows in the table — one per table, usually the primary key. Non-clustered index is a separate structure with pointers to the actual rows; a table can have multiple non-clustered indexes."),
            q(7,  "What is a transaction?",
               "A transaction is a sequence of one or more SQL operations executed as a single logical unit of work. It either commits (all changes saved) or rolls back (all changes undone) to ensure database consistency."),
            q(8,  "What is the difference between DELETE, TRUNCATE, and DROP?",
               "DELETE: removes specific rows, can be rolled back, fires triggers, slower. TRUNCATE: removes all rows, cannot be rolled back (in most DBs), no triggers, faster. DROP: removes the entire table structure and data permanently."),
            q(9,  "What is the difference between WHERE and HAVING?",
               "WHERE filters rows before grouping (cannot use aggregate functions). HAVING filters groups after GROUP BY (can use aggregate functions like COUNT, SUM). Example: WHERE salary > 50000 vs HAVING COUNT(*) > 5.")
        );
    }

    private List<CachedQuestion> buildSqlQueryQuestions() {
        return List.of(
            q(1,  "Write a query to find the second-highest salary.",
               "SELECT MAX(salary) FROM employees WHERE salary < (SELECT MAX(salary) FROM employees); — Or using LIMIT: SELECT salary FROM employees ORDER BY salary DESC LIMIT 1 OFFSET 1;"),
            q(2,  "Find employees earning more than the average salary.",
               "SELECT * FROM employees WHERE salary > (SELECT AVG(salary) FROM employees);"),
            q(3,  "Find the highest salary in each department.",
               "SELECT department_id, MAX(salary) AS max_salary FROM employees GROUP BY department_id;"),
            q(4,  "Find duplicate records in a table.",
               "SELECT email, COUNT(*) FROM employees GROUP BY email HAVING COUNT(*) > 1;"),
            q(5,  "Explain INNER JOIN, LEFT JOIN, RIGHT JOIN, and FULL OUTER JOIN.",
               "INNER JOIN: rows with matching values in both tables. LEFT JOIN: all rows from left + matched from right (NULL if no match). RIGHT JOIN: all rows from right + matched from left. FULL OUTER JOIN: all rows from both tables (NULLs where no match)."),
            q(6,  "Find the top three highest-paid employees.",
               "SELECT * FROM employees ORDER BY salary DESC LIMIT 3;"),
            q(7,  "Delete duplicate rows while keeping one copy.",
               "DELETE FROM employees WHERE id NOT IN (SELECT MIN(id) FROM employees GROUP BY email);"),
            q(8,  "What is the difference between UNION and UNION ALL?",
               "UNION removes duplicate rows from the combined result set. UNION ALL includes all rows including duplicates, making it faster. Use UNION ALL when you know there are no duplicates or duplicates are acceptable."),
            q(9,  "Find employees who earn more than their department's average salary.",
               "SELECT e.* FROM employees e JOIN (SELECT department_id, AVG(salary) avg_sal FROM employees GROUP BY department_id) d ON e.department_id = d.department_id WHERE e.salary > d.avg_sal;")
        );
    }

    private List<CachedQuestion> buildSpringBootQuestions() {
        return List.of(
            q(1,  "What is Spring Boot, and how does it differ from Spring?",
               "Spring Boot is an opinionated framework built on top of Spring that auto-configures dependencies, embeds a web server (Tomcat), and provides production-ready features out of the box. Spring requires manual XML/Java configuration for every component."),
            q(2,  "What is dependency injection (DI)?",
               "DI is a design pattern where an object's dependencies are provided by an external source rather than the object creating them itself. In Spring, the IoC container manages object creation and injects dependencies via @Autowired, constructor injection, or setter injection."),
            q(3,  "What is inversion of control (IoC)?",
               "IoC inverts the traditional flow: instead of the programmer controlling object creation and lifecycle, the framework (Spring IoC container) controls it. This decouples components, making the code more testable and modular."),
            q(4,  "Explain @Component, @Service, and @Repository.",
               "@Component is the generic stereotype for a Spring-managed bean. @Service specializes it for business logic layer. @Repository specializes it for data access layer and adds automatic exception translation (DataAccessException). All three trigger component scanning."),
            q(5,  "What is the difference between @Controller and @RestController?",
               "@Controller returns view names resolved by a ViewResolver (for server-side rendered HTML). @RestController = @Controller + @ResponseBody, meaning every method automatically serializes the return value to JSON/XML for REST APIs."),
            q(6,  "What is the difference between constructor injection and field injection?",
               "Constructor injection (recommended): dependencies passed through the constructor — makes dependencies explicit, supports immutability, easier to test. Field injection (@Autowired on field): convenient but hides dependencies and makes unit testing harder without Spring context."),
            q(7,  "What is Spring Data JPA?",
               "Spring Data JPA is an abstraction over JPA that eliminates boilerplate DAO code. By extending JpaRepository<Entity, ID>, you get CRUD methods (save, findById, findAll, delete) for free, plus support for derived query methods like findByEmail(String email)."),
            q(8,  "What is the purpose of application.properties?",
               "application.properties (or application.yml) is Spring Boot's central configuration file. It configures server port, database connection, logging levels, API keys, cache settings, and any custom application properties read via @Value or @ConfigurationProperties."),
            q(9,  "How does Spring Boot connect to MySQL?",
               "Add spring-boot-starter-data-jpa and mysql-connector-j dependencies. In application.properties set: spring.datasource.url, spring.datasource.username, spring.datasource.password, and spring.jpa.hibernate.ddl-auto. Spring Boot auto-configures the DataSource and EntityManager."),
            q(10, "What is Maven, and what is the purpose of pom.xml?",
               "Maven is a build automation tool. pom.xml (Project Object Model) declares project metadata, dependencies, plugins, and build lifecycle. Maven resolves transitive dependencies from Maven Central and runs lifecycle phases: validate → compile → test → package → install → deploy.")
        );
    }

    private List<CachedQuestion> buildRestApiQuestions() {
        return List.of(
            q(1,  "What is a REST API?",
               "REST (Representational State Transfer) is an architectural style for distributed systems. A REST API uses HTTP methods (GET, POST, PUT, DELETE) to perform CRUD operations on resources identified by URLs, returning data in JSON or XML."),
            q(2,  "What are the principles of REST?",
               "Client-Server separation, Statelessness (no session on the server), Cacheability, Uniform Interface (consistent resource URLs + HTTP methods), Layered System, and optionally Code on Demand."),
            q(3,  "Explain GET, POST, PUT, PATCH, and DELETE.",
               "GET: retrieve resource (idempotent, safe). POST: create new resource (not idempotent). PUT: replace entire resource (idempotent). PATCH: partial update (not necessarily idempotent). DELETE: remove resource (idempotent)."),
            q(4,  "Explain HTTP status codes 200, 201, 400, 401, 403, 404, 500.",
               "200 OK, 201 Created, 400 Bad Request (client error in input), 401 Unauthorized (authentication required), 403 Forbidden (authenticated but no permission), 404 Not Found, 500 Internal Server Error."),
            q(5,  "How do you handle exceptions in a REST API?",
               "Use @ControllerAdvice with @ExceptionHandler methods to return structured error responses (error code, message, timestamp). Spring Boot's ResponseEntityExceptionHandler can be extended for built-in exception types."),
            q(6,  "What is CORS?",
               "Cross-Origin Resource Sharing is a browser security mechanism that blocks web pages from making requests to a different domain than the one that served the page. In Spring Boot, @CrossOrigin on a controller or a global WebMvcConfigurer bean configures allowed origins."),
            q(7,  "What is the difference between authentication and authorization?",
               "Authentication verifies who you are (login with username/password, JWT token). Authorization determines what you are allowed to do (role-based access: ADMIN can delete, USER can only read). Authentication comes first."),
            q(8,  "How do you validate incoming API requests?",
               "Add spring-boot-starter-validation. Annotate DTOs with @NotBlank, @Size, @Email, @Min, @Max. Add @Valid on the controller method parameter. A MethodArgumentNotValidException is thrown automatically on violation — catch it with @ExceptionHandler.")
        );
    }

    private List<CachedQuestion> buildDsaArraysQuestions() {
        return List.of(
            q(1,  "What is the time complexity of accessing an array element?",
               "O(1) — constant time. Arrays store elements in contiguous memory, so any element can be accessed directly via its index (base_address + index × element_size) regardless of the array's size."),
            q(2,  "Find the second-largest element in an array.",
               "Single pass O(n): track both 'max' and 'secondMax'. Initialize both to Integer.MIN_VALUE. For each element: if element > max, update secondMax = max, max = element; else if element > secondMax and element != max, update secondMax = element."),
            q(3,  "Explain Kadane's algorithm.",
               "Kadane's finds the maximum subarray sum in O(n). Variables: maxSoFar = INT_MIN, currentMax = 0. For each element: currentMax = max(element, currentMax + element); maxSoFar = max(maxSoFar, currentMax). Result: maxSoFar."),
            q(4,  "Explain the two-pointer technique.",
               "Two pointers (usually left/right) start at opposite ends of a sorted array and move toward each other. Used for: pair sum, container with most water, palindrome checks. Reduces O(n²) brute force to O(n)."),
            q(5,  "What is the sliding-window technique?",
               "Maintain a window (subarray) of fixed or variable size as it slides over the array. For fixed size: add new right element, remove old left element. Used for: max sum of k elements, longest substring without repeats. O(n) instead of O(n·k)."),
            q(6,  "Check whether a string is a palindrome.",
               "Use two pointers: left = 0, right = s.length()-1. While left < right, compare s.charAt(left) and s.charAt(right). If any pair mismatches, return false. If loop completes, return true. O(n) time, O(1) space."),
            q(7,  "Check whether two strings are anagrams.",
               "Method 1: sort both strings and compare — O(n log n). Method 2: use a frequency array/HashMap of size 26 for characters. Increment for string1, decrement for string2. If all counts are 0, they're anagrams — O(n)."),
            q(8,  "Find two numbers in an array whose sum equals a target.",
               "Use a HashMap: for each element x, check if (target - x) exists in the map. If yes, return the pair. If no, store x in the map. O(n) time, O(n) space. Sorted array alternative: two-pointer O(n) time, O(1) space.")
        );
    }

    private List<CachedQuestion> buildDsaSearchSortQuestions() {
        return List.of(
            q(1,  "What is binary search, and what is its time complexity?",
               "Binary search finds a target in a sorted array by repeatedly halving the search space. Compare target to mid element: if equal, found; if less, search left half; if greater, search right half. Time: O(log n), Space: O(1) iterative."),
            q(2,  "Explain bubble sort, selection sort, and insertion sort.",
               "Bubble sort: repeatedly swap adjacent out-of-order pairs — O(n²). Selection sort: find minimum in unsorted portion, place at front — O(n²). Insertion sort: insert each element into its correct position in the sorted portion — O(n²) worst, O(n) best (nearly sorted)."),
            q(3,  "What are merge sort and quicksort?",
               "Merge sort: divide array in half, recursively sort, merge — O(n log n) always, O(n) extra space. Quicksort: pick a pivot, partition into <pivot and >pivot, recurse — O(n log n) average, O(n²) worst case, O(log n) extra space."),
            q(4,  "What is Big O notation?",
               "Big O describes the upper bound on algorithm runtime or space as input size n grows. It ignores constants and lower-order terms to focus on growth rate. Used to compare algorithm efficiency independent of hardware."),
            q(5,  "Explain O(1), O(log n), O(n), O(n log n), and O(n²).",
               "O(1): constant — array access. O(log n): binary search. O(n): linear scan. O(n log n): merge sort, quicksort average. O(n²): bubble/selection/insertion sort, nested loops. Best to worst in practice."),
            q(6,  "How do you analyze the complexity of nested loops?",
               "For two nested loops each running n times: O(n²). If the inner loop runs n/2 times on average, still O(n²) — constants are dropped. If outer is n and inner is log n, overall is O(n log n). Multiply the complexities of independent nested loops.")
        );
    }

    private List<CachedQuestion> buildDsaStructuresQuestions() {
        return List.of(
            q(1,  "What is the difference between an array and a linked list?",
               "Array: contiguous memory, O(1) access by index, O(n) insert/delete (shifting). Linked list: non-contiguous nodes with pointers, O(n) access by index, O(1) insert/delete at known node. Array is cache-friendly; linked list has extra pointer overhead."),
            q(2,  "How do you detect a cycle in a linked list?",
               "Floyd's Cycle Detection (fast-slow pointers): slow moves 1 step, fast moves 2 steps. If they meet, there's a cycle. If fast reaches null, no cycle. O(n) time, O(1) space."),
            q(3,  "How do you find the middle node of a linked list?",
               "Use slow/fast pointers: both start at head. Move slow 1 step, fast 2 steps. When fast reaches the end (null or last node), slow is at the middle. O(n) time, O(1) space."),
            q(4,  "What is the difference between a stack and a queue?",
               "Stack: LIFO (Last In First Out) — push/pop at the top. Used for: undo operations, recursion call stack, balanced parentheses. Queue: FIFO (First In First Out) — enqueue at rear, dequeue at front. Used for: BFS, task scheduling, print queues."),
            q(5,  "What is a priority queue?",
               "A priority queue is an abstract data type where each element has a priority. The element with the highest (or lowest) priority is dequeued first, regardless of insertion order. Java's PriorityQueue uses a min-heap internally — O(log n) insert/remove."),
            q(6,  "What are tree traversals?",
               "In-order (Left→Root→Right): gives sorted output for BST. Pre-order (Root→Left→Right): used for tree copying. Post-order (Left→Right→Root): used for tree deletion. Level-order (BFS using queue): visits nodes level by level."),
            q(7,  "What is the difference between a binary tree and a binary search tree?",
               "Binary tree: each node has at most 2 children — no ordering constraint. BST: a binary tree where left subtree values < node < right subtree values, enabling O(log n) search, insert, delete in a balanced BST.")
        );
    }

    private List<CachedQuestion> buildPlacementTrackerQuestions() {
        return List.of(
            q(1,  "Explain your Student Placement Tracker project.",
               "The Student Placement Tracker is a full-stack Java web application built with Spring Boot and MySQL. It allows students to view companies visiting campus, submit placement applications, and track their application status. Administrators can manage companies, update placement results, and view analytics."),
            q(2,  "Explain the architecture of your project.",
               "Three-tier architecture: Presentation layer (REST API controllers returning JSON), Business logic layer (Service classes), and Data layer (Spring Data JPA Repositories + MySQL). Each layer communicates only with the adjacent layer, maintaining separation of concerns."),
            q(3,  "What tables did you create in MySQL, and why?",
               "Students table (id, name, email, branch, cgpa), Companies table (id, name, role, package_lpa, eligibility_criteria), Applications table (id, student_id FK, company_id FK, status, applied_date). The Applications table is a junction table implementing many-to-many between students and companies."),
            q(4,  "Which REST endpoints does your application expose?",
               "POST /students (register), GET /students/{id}, POST /companies (admin adds company), GET /companies, POST /applications (student applies), GET /applications/{studentId} (track status), PUT /applications/{id}/status (admin updates result)."),
            q(5,  "How would you prevent duplicate placement applications?",
               "Add a UNIQUE constraint on (student_id, company_id) in the Applications table. In the service layer, check for existing application before inserting. Catch DataIntegrityViolationException and return a meaningful 409 Conflict response."),
            q(6,  "How would you implement login and role-based access?",
               "Add Spring Security. Create a Users table with roles (STUDENT, ADMIN). Use JWT tokens: login endpoint returns a signed JWT, subsequent requests include it in Authorization header. @PreAuthorize('hasRole(ADMIN)') guards admin-only endpoints."),
            q(7,  "How would you deploy the application for real users?",
               "Package as a JAR with mvn package. Deploy to a cloud VM (AWS EC2 or GCP Compute Engine), use MySQL RDS for the database, configure environment variables for credentials, and use Nginx as a reverse proxy. Use Docker for containerization.")
        );
    }

    private List<CachedQuestion> buildAiGeneratorQuestions() {
        return List.of(
            q(1,  "How does your AI Interview Generator work?",
               "The user provides a job title, experience level, question type, and language. Spring Boot sends a carefully crafted prompt to the Google Gemini API via WebClient. Gemini returns structured JSON with categorized questions and answers. The backend parses and returns it; the frontend renders it with show/hide answer toggles."),
            q(2,  "What is prompt engineering, and how did you use it?",
               "Prompt engineering is crafting the input text to guide an LLM toward a specific, structured output. I instructed Gemini to: return only valid JSON (no markdown), use a fixed schema with categories and Q&A pairs, tailor difficulty to the experience level, and respond in the selected language."),
            q(3,  "How do you manage API keys securely?",
               "The API key is externalized in application.properties and loaded via @Value. In production, it would be set as an environment variable (export GEMINI_API_KEY=...) or stored in a secrets manager (AWS Secrets Manager, GCP Secret Manager) — never hardcoded in source code."),
            q(4,  "What happens when the Gemini API returns an error or times out?",
               "The service catches all exceptions from the WebClient call. On error, it falls back to the pre-loaded QuestionBankService cache, which selects the most relevant cached questions based on job title, level, and type. Users always receive questions — the fallback is transparent to them."),
            q(5,  "How would you handle API rate limits?",
               "Implement an in-memory cache (e.g., Caffeine) keyed by (jobTitle + level + type) to avoid identical API calls. Add exponential backoff retry logic using WebClient's retryWhen() operator. Use a token bucket (Bucket4j) for rate limiting on our own API endpoint."),
            q(6,  "What are the limitations of generative AI?",
               "Hallucinations (generating plausible but incorrect information), inconsistent output format, token/rate limits, latency (~2-5s), cost at scale, difficulty controlling output quality, and lack of real-time knowledge. These must be mitigated through prompt design and validation.")
        );
    }

    private List<CachedQuestion> buildSdlcQuestions() {
        return List.of(
            q(1,  "What is the Software Development Life Cycle (SDLC)?",
               "SDLC is a structured process for planning, creating, testing, and deploying software. Phases: Planning → Requirements → System Design → Implementation → Testing → Deployment → Maintenance. It ensures quality and project manageability."),
            q(2,  "What is the difference between Agile and Waterfall?",
               "Waterfall: sequential phases, rigid requirements upfront, hard to accommodate changes. Agile: iterative sprints (1-4 weeks), continuous delivery, adaptive to changing requirements. Agile suits dynamic projects; Waterfall suits well-defined, stable requirements."),
            q(3,  "What is version control? What is Git?",
               "Version control tracks changes to files over time, enabling collaboration and rollback. Git is a distributed VCS where every developer has a full copy of the repository. Key commands: clone, add, commit, push, pull, branch, merge, rebase."),
            q(4,  "What is the difference between Git and GitHub?",
               "Git is the version control tool that runs locally on your machine. GitHub is a cloud-based hosting platform for Git repositories that adds collaboration features: pull requests, code reviews, issue tracking, and CI/CD via GitHub Actions."),
            q(5,  "What is CI/CD?",
               "Continuous Integration: developers frequently merge code into a shared branch; automated builds and tests run on every push to detect issues early. Continuous Delivery/Deployment: automatically deploys passing builds to staging or production, enabling rapid, reliable releases."),
            q(6,  "What is the difference between monolithic and microservices architecture?",
               "Monolithic: entire application deployed as one unit — simple to develop and deploy but hard to scale and maintain as it grows. Microservices: small, independent services communicating via APIs — each can be deployed, scaled, and updated independently, but adds operational complexity.")
        );
    }

    private List<CachedQuestion> buildNetworksQuestions() {
        return List.of(
            q(1,  "Explain the OSI model and its seven layers.",
               "Physical (bits on wire), Data Link (MAC addresses, frames), Network (IP routing), Transport (TCP/UDP, ports), Session (connection management), Presentation (encoding, encryption), Application (HTTP, DNS, FTP). Each layer serves the layer above it."),
            q(2,  "What is the difference between TCP and UDP?",
               "TCP: connection-oriented (3-way handshake), reliable (acknowledgments, retransmission), ordered delivery, slower. UDP: connectionless, unreliable (no ACK), no ordering, faster. TCP for web/file transfer; UDP for video streaming, gaming, DNS."),
            q(3,  "What happens when you type a URL into a browser?",
               "DNS resolves the domain to an IP. TCP connection established (3-way handshake). TLS handshake for HTTPS. HTTP GET request sent. Server processes and returns HTML. Browser parses HTML, downloads CSS/JS, renders the page."),
            q(4,  "What is the difference between HTTP and HTTPS?",
               "HTTP transmits data in plaintext — vulnerable to man-in-the-middle attacks. HTTPS encrypts data using TLS (Transport Layer Security), provides server authentication via certificates, and ensures data integrity. All modern web apps must use HTTPS."),
            q(5,  "What is DNS?",
               "Domain Name System translates human-readable domain names (google.com) to IP addresses (142.250.80.46). It's a distributed hierarchical database: Root servers → TLD servers (.com) → Authoritative name servers for the specific domain.")
        );
    }

    private List<CachedQuestion> buildOsQuestions() {
        return List.of(
            q(1,  "What is the difference between a process and a thread?",
               "Process: independent program instance with its own memory space, PCB, and resources. Thread: lightweight unit of execution within a process, sharing the process's memory and resources. Context-switching between threads is faster than between processes."),
            q(2,  "What is deadlock? Explain its necessary conditions.",
               "Deadlock: two or more processes wait for resources held by each other indefinitely. Necessary conditions (all four must hold): Mutual Exclusion, Hold and Wait, No Preemption, Circular Wait. Breaking any one condition prevents deadlock."),
            q(3,  "What is virtual memory?",
               "Virtual memory is a memory management technique that gives each process the illusion of having a large, contiguous address space larger than physical RAM. Pages are swapped between RAM and disk (swap space) on demand. Enables running programs larger than available RAM."),
            q(4,  "Explain CPU scheduling algorithms.",
               "FCFS: first come first served — simple but convoy effect. SJF: shortest job first — minimizes average waiting time but requires knowing burst time. Round Robin: time quantum per process — fair, good for time-sharing. Priority scheduling: highest priority runs first — risk of starvation (solved by aging)."),
            q(5,  "What is a semaphore?",
               "A semaphore is a synchronization primitive — an integer variable accessed via two atomic operations: wait()/P() (decrement; block if < 0) and signal()/V() (increment; wake a blocked process). Binary semaphore = mutex. Counting semaphore controls access to a pool of resources.")
        );
    }

    private List<CachedQuestion> buildCloudQuestions() {
        return List.of(
            q(1,  "Explain IaaS, PaaS, and SaaS.",
               "IaaS (Infrastructure as a Service): raw compute, storage, networking — you manage OS and up (AWS EC2). PaaS (Platform as a Service): managed runtime and middleware — you manage the app (Heroku, Google App Engine). SaaS (Software as a Service): fully managed application (Gmail, Salesforce)."),
            q(2,  "What is Zero Trust Cloud Security?",
               "Zero Trust: 'never trust, always verify'. No user or device is trusted by default, even inside the network. Every access request is authenticated, authorized, and continuously validated. Implements least privilege, micro-segmentation, and multi-factor authentication."),
            q(3,  "What is encryption, and how would you protect sensitive data?",
               "Encryption converts plaintext to ciphertext using an algorithm and key. In transit: use HTTPS/TLS. At rest: encrypt database columns (AES-256). Never log sensitive data. Store passwords using bcrypt (salted hash). Use environment variables for secrets — never commit them to source control."),
            q(4,  "What is multi-factor authentication (MFA)?",
               "MFA requires users to provide two or more verification factors: something you know (password), something you have (OTP from authenticator app), something you are (biometric). Even if a password is stolen, the account remains protected.")
        );
    }

    private List<CachedQuestion> buildCodingQuestions() {
        return List.of(
            q(1,  "Find the maximum subarray sum (Kadane's algorithm).",
               "int maxSum = arr[0], current = arr[0]; for(int i=1; i<arr.length; i++) { current = Math.max(arr[i], current+arr[i]); maxSum = Math.max(maxSum, current); } return maxSum; — O(n) time, O(1) space."),
            q(2,  "Count character frequency using HashMap.",
               "Map<Character,Integer> freq = new HashMap<>(); for(char c : s.toCharArray()) freq.merge(c, 1, Integer::sum); — Then iterate freq.entrySet() to print each character and its count. O(n) time."),
            q(3,  "Find the longest substring without repeating characters.",
               "Sliding window + HashSet: left=0, maxLen=0. For each right pointer: while set contains s[right], remove s[left] and left++. Add s[right] to set. maxLen = max(maxLen, right-left+1). O(n) time."),
            q(4,  "Remove duplicates from a sorted array (in-place).",
               "int k=1; for(int i=1; i<arr.length; i++) { if(arr[i] != arr[i-1]) { arr[k++] = arr[i]; } } return k; — Modifies array in-place, returns count of unique elements. O(n) time, O(1) space."),
            q(5,  "Check whether a number is prime.",
               "If n<=1 return false. Check divisors from 2 to sqrt(n). If any divides n evenly, not prime. Otherwise prime. O(√n) time. Optimization: check 2 separately, then only odd numbers."),
            q(6,  "Detect a cycle in a linked list (Floyd's algorithm).",
               "ListNode slow=head, fast=head; while(fast!=null && fast.next!=null) { slow=slow.next; fast=fast.next.next; if(slow==fast) return true; } return false; — O(n) time, O(1) space."),
            q(7,  "Find the middle of a linked list.",
               "ListNode slow=head, fast=head; while(fast!=null && fast.next!=null) { slow=slow.next; fast=fast.next.next; } return slow; — When fast reaches end, slow is at the middle. O(n) time, O(1) space."),
            q(8,  "Check if a string is a palindrome.",
               "int left=0, right=s.length()-1; while(left<right) { if(s.charAt(left)!=s.charAt(right)) return false; left++; right--; } return true; — O(n) time, O(1) space."),
            q(9,  "Generate the Fibonacci sequence up to n terms.",
               "int a=0, b=1; for(int i=0; i<n; i++) { System.out.print(a+\" \"); int c=a+b; a=b; b=c; } — Iterative O(n), O(1) space. Recursive is O(2^n) — avoid for large n."),
            q(10, "Find two numbers that sum to a target (Two Sum).",
               "Map<Integer,Integer> map = new HashMap<>(); for(int i=0; i<nums.length; i++) { int comp=target-nums[i]; if(map.containsKey(comp)) return new int[]{map.get(comp),i}; map.put(nums[i],i); } — O(n) time, O(n) space.")
        );
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private static CachedQuestion q(int number, String question, String answer) {
        return new CachedQuestion(number, question, answer);
    }
}
