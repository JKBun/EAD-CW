# Hotel Management System (EAD Coursework)

Java Swing + MySQL + Ant, built for the Hospitality & Tourism enterprise app coursework.

## What's in this project

- All 6 forms: Login, Dashboard, Room Management, Customer Management, Booking, Reports
- `UserDAO`, `RoomDAO`, `CustomerDAO`, `BookingDAO` (DAO pattern) + `DBConnection` (Singleton pattern)
- Custom exceptions: `InvalidLoginException`, `RoomNotAvailableException`
- Full MySQL schema with sample data: `sql/schema.sql`
- JasperReports template: `src/reports/BookingsReport.jrxml`
- Ant `build.xml` so it compiles the same way outside NetBeans

## Forms (6 total, meets the "at least 5" requirement)

1. ✅ **Login** — authentication, custom exception
2. ✅ **Dashboard** — navigation hub, shows logged-in user
3. ✅ **Room Management** (Input UI) — full CRUD for rooms
4. ✅ **Customer Management** (Input UI) — full CRUD for guests
5. ✅ **Booking** (Transaction UI — the "major functionality") — creates a booking joining customers + rooms, updates room status, wrapped in a real DB transaction
6. ✅ **Reports** — JasperReports output pulling from bookings + customers + rooms (3 tables)

All 6 are built and wired up from the Dashboard.

## Setup steps

### 1. Start MySQL in XAMPP
Open the XAMPP Control Panel and click **Start** next to MySQL.

### 2. Create the database
Open phpMyAdmin (`http://localhost/phpmyadmin`) → **Import** tab → choose
`sql/schema.sql` → Go. (Or paste its contents into the SQL tab and run it.)

This creates the `hotel_management_system` database, all 4 tables, and 2 sample logins:
- `admin` / `admin123`
- `reception` / `reception123`

### 3. Add the MySQL JDBC driver
Copy your `mysql-connector-j-8.1.0.jar` into the `lib/` folder of this project
(it's already referenced by name in `build.xml`).

In NetBeans: right-click the project → Properties → Libraries → Add JAR/Folder → select the jar.

### 4. DB password
XAMPP's default MySQL `root` user has **no password**, and runs on the default port 3306 —
`DBConnection.java` is already set up for this (`DB_USER = "root"`, `DB_PASSWORD = ""`).
You shouldn't need to change anything unless you've customized your XAMPP MySQL setup.

### 5. Open in NetBeans
File → Open Project → select this folder. Because it has a `build.xml`, NetBeans
will recognize it as a **Free-Form/Ant project**. If you'd rather use NetBeans' own
project format, create a new "Java with Ant" project and copy the `src/` package
(`hotelapp`) into it — the code doesn't need to change.

### 6. Add the JasperReports library (for the Reports form)
Since you already have the Jasper plugins installed in NetBeans:

1. In NetBeans, go to **Tools → Libraries**
2. Look for a library called something like **JasperReports** or **iReport** in the list on the left
3. If it exists, right-click the project → Properties → Libraries → Add Library → select it
4. If it doesn't exist as a ready-made library, you'll need these jars in your `lib/` folder
   (they usually come bundled with the iReport/Jaspersoft Studio plugin install folder — look
   in its `ireport/modules/ext/` or similar folder):
   - `jasperreports-x.x.x.jar`
   - `commons-collections4-x.x.jar`
   - `commons-digester3-x.x.jar`
   - `commons-logging-x.x.jar`
   - `commons-beanutils-x.x.jar`

If NetBeans complains about missing classes like `net.sf.jasperreports.engine.*` when building,
that means one of these jars isn't on the project's classpath yet — add whichever jar contains
the missing class.

The report template itself (`src/reports/BookingsReport.jrxml`) is compiled automatically at
runtime when you click "Generate Bookings Report" — you don't need iReport/Jaspersoft Studio
open to use it, only its libraries.

### 7. Run
- In NetBeans: right-click → Run
- From terminal: `ant run` (requires Apache Ant installed)
- Or: `ant jar` then `java -jar dist/HotelManagementSystem.jar`

## Building the .exe deliverable

The coursework wants both a `.jar` and a `.exe`. The jar already comes out of
`ant jar`. For the `.exe`, use **Launch4j** (a free tool that wraps a jar into
a Windows executable) — a config file (`l4j-config.xml`) is already included.

1. Download Launch4j: https://launch4j.sourceforge.net/ and install it
2. Make sure you've already run `ant jar` (or built/run once in NetBeans) so
   `dist/HotelManagementSystem.jar` exists
3. Open Launch4j → **File → Open** → select `l4j-config.xml` from this project
4. Click the gear/cog icon (**Build wrapper**) — it'll produce
   `dist/HotelManagementSystem.exe`
5. Double-click the exe to test it (make sure MySQL/XAMPP is running first)

Note: the exe still needs a Java Runtime installed on whatever machine runs it
(it doesn't bundle one) — that's fine for submission/demo purposes on your own
PC or your lecturer's, as long as Java is installed.

If Launch4j complains it can't find a JRE, open `l4j-config.xml` and check the
`<minVersion>` tag isn't higher than the Java version installed on the test machine.

## Submitting via Git / GitHub

Your coursework wants a GitHub repo link, not a zip. From the project's root folder
(the one with `build.xml` in it):

```bash
git init
git add .
git commit -m "Initial commit - Hotel Management System"
```

Then on GitHub.com:
1. Create a new **empty** repository (don't tick "Add a README" — you already have one)
2. Copy the remote URL it gives you (looks like `https://github.com/yourname/HotelManagementSystem.git`)
3. Back in your terminal:
```bash
git remote add origin https://github.com/yourname/HotelManagementSystem.git
git branch -M main
git push -u origin main
```

After that, keep committing as you make changes so there's a real commit history
(lecturers sometimes check this) — e.g. after each form you add or fix:
```bash
git add .
git commit -m "Added colour theme to all forms"
git push
```

A `.gitignore` is included so build output (`build/`, `dist/`) doesn't get committed —
only source code, the schema, and the report template will be tracked. The `lib/`
folder (with your mysql-connector jar) IS tracked on purpose, so anyone cloning the
repo has everything they need to build it.



- **Design patterns used:** Singleton (`DBConnection` — only one DB connection object exists), DAO (`UserDAO`, `RoomDAO`, `CustomerDAO`, `BookingDAO` — all SQL is separated from the UI)
- **MVC:** `model` package = data objects, `view` package = Swing UI, `dao` package = the "controller"-ish data layer talking to MySQL
- **Transactions:** `BookingDAO.createBooking()` wraps the booking insert + room status update in a single JDBC transaction (`setAutoCommit(false)` ... `commit()`/`rollback()`), with `SELECT ... FOR UPDATE` to prevent two staff members double-booking the same room at the same time
- **Validation:** empty-field checks, phone/email regex, price > 0, check-out date after check-in date, all done before hitting the database
- **Custom exceptions:** `InvalidLoginException` (bad login), `RoomNotAvailableException` (room taken between viewing and booking)
- **Reports:** `ReportForm` compiles `BookingsReport.jrxml` at runtime with `JasperCompileManager`, fills it with data joined from bookings + customers + rooms, and previews it with `JasperViewer`. Shows a running revenue total and booking count using JasperReports variables.
- **Passwords are stored in plain text** in this starter for simplicity — mention to your lecturer you know this isn't production-safe, and optionally hash with `BCrypt` or `SHA-256` before the deadline if marks are awarded for it (ask if you want me to add this).
