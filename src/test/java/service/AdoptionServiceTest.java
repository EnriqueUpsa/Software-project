package service;

import dao.AdoptionDAO;
import dao.InMemoryAdopterDAO;
import dao.InMemoryAnimalDAO;
import dao.InMemoryStatusChangeLogDAO;
import dao.StatusChangeLogDAO;
import model.Adopter;
import model.Adoption;
import model.Animal;
import model.Dog;
import model.StatusChangeLog;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdoptionServiceTest {

    private static class FailingAdoptionDAO implements AdoptionDAO {
        @Override
        public void save(Adoption adoption) {
            throw new RuntimeException("DB error");
        }

        @Override
        public java.util.Map<Month, Integer> getMonthlyAdoptions(int year) {
            return Collections.emptyMap();
        }
    }

    private static class TrackingAnimalDAO extends InMemoryAnimalDAO {
        private final Set<String> updatedIds = new HashSet<>();

        @Override
        public void update(Animal animal) {
            super.update(animal);
            updatedIds.add(animal.getMicrochipId());
        }
    }

    private static class FailingStatusChangeLogDAO implements StatusChangeLogDAO {
        @Override
        public void save(StatusChangeLog log) {
            throw new RuntimeException("Audit insert error");
        }

        @Override
        public java.util.List<StatusChangeLog> findByAnimal(String microchipId) {
            return Collections.emptyList();
        }
    }

    private static class FakeConnection implements Connection {
        private boolean autoCommit = true;
        private boolean rolledBack = false;
        private boolean committed = false;

        @Override
        public boolean getAutoCommit() {
            return autoCommit;
        }

        @Override
        public void setAutoCommit(boolean autoCommit) {
            this.autoCommit = autoCommit;
        }

        @Override
        public void commit() {
            committed = true;
        }

        @Override
        public void rollback() {
            rolledBack = true;
        }

        public boolean wasCommitted() {
            return committed;
        }

        public boolean wasRolledBack() {
            return rolledBack;
        }

        @Override
        public void close() {
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.Statement createStatement() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType,
                                                      int resultSetConcurrency)
                throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType,
                                                      int resultSetConcurrency,
                                                      int resultSetHoldability)
                throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getCatalog() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.SQLWarning getWarnings() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clearWarnings() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.DatabaseMetaData getMetaData() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getHoldability() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.Savepoint setSavepoint() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.Savepoint setSavepoint(String name) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void rollback(java.sql.Savepoint savepoint) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency)
                throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency,
                                                  int resultSetHoldability) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType,
                                                           int resultSetConcurrency)
                throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType,
                                                           int resultSetConcurrency,
                                                           int resultSetHoldability)
                throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
                throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes)
                throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames)
                throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.Clob createClob() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.Blob createBlob() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.NClob createNClob() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.SQLXML createSQLXML() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setClientInfo(String name, String value)
                throws java.sql.SQLClientInfoException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setClientInfo(java.util.Properties properties)
                throws java.sql.SQLClientInfoException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.util.Properties getClientInfo() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.Array createArrayOf(String typeName, Object[] elements)
                throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.sql.Struct createStruct(String typeName, Object[] attributes)
                throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSchema(String schema) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSchema() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void abort(java.util.concurrent.Executor executor) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds)
                throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    void processAdoption_valid_succeeds() {
        TrackingAnimalDAO animalDAO = new TrackingAnimalDAO();
        AdoptionDAO adoptionDAO = new dao.InMemoryAdoptionDAO();
        InMemoryStatusChangeLogDAO logDAO = new InMemoryStatusChangeLogDAO();
        FakeConnection connection = new FakeConnection();
        AdoptionService service =
                new AdoptionService(adoptionDAO, animalDAO, logDAO, connection);

        Dog dog = new Dog("A-1", "Labrador", LocalDate.now(),
                Animal.Status.READY_FOR_ADOPTION);
        animalDAO.save(dog);
        Adoption adoption = new Adoption("A-1", "U-1", LocalDate.now());

        service.processAdoption(dog, adoption);

        assertEquals(Animal.Status.ADOPTED, dog.getStatus());
        assertEquals(1, logDAO.findByAnimal("A-1").size());
        assertTrue(connection.wasCommitted());
        assertTrue(connection.getAutoCommit());
    }

    @Test
    void processAdoption_notEligible_throwsException() {
        TrackingAnimalDAO animalDAO = new TrackingAnimalDAO();
        AdoptionDAO adoptionDAO = new dao.InMemoryAdoptionDAO();
        FakeConnection connection = new FakeConnection();
        AdoptionService service = new AdoptionService(adoptionDAO, animalDAO, connection);

        Dog dog = new Dog("A-2", "Beagle", LocalDate.now(),
                Animal.Status.UNDER_TREATMENT);
        Adoption adoption = new Adoption("A-2", "U-2", LocalDate.now());

        assertThrows(IllegalArgumentException.class,
                () -> service.processAdoption(dog, adoption));
    }

    @Test
    void processAdoption_duplicate_throwsException() {
        TrackingAnimalDAO animalDAO = new TrackingAnimalDAO();
        AdoptionDAO adoptionDAO = new dao.InMemoryAdoptionDAO();
        FakeConnection connection = new FakeConnection();
        AdoptionService service = new AdoptionService(adoptionDAO, animalDAO, connection);

        Dog dog = new Dog("A-3", "Pug", LocalDate.now(),
                Animal.Status.ADOPTED);
        Adoption adoption = new Adoption("A-3", "U-3", LocalDate.now());

        assertThrows(IllegalArgumentException.class,
                () -> service.processAdoption(dog, adoption));
    }

    @Test
    void processAdoption_failure_rollsBack() {
        TrackingAnimalDAO animalDAO = new TrackingAnimalDAO();
        AdoptionDAO adoptionDAO = new FailingAdoptionDAO();
        InMemoryStatusChangeLogDAO logDAO = new InMemoryStatusChangeLogDAO();
        FakeConnection connection = new FakeConnection();
        AdoptionService service =
                new AdoptionService(adoptionDAO, animalDAO, logDAO, connection);

        Dog dog = new Dog("A-4", "Bulldog", LocalDate.now(),
                Animal.Status.READY_FOR_ADOPTION);
        animalDAO.save(dog);
        Adoption adoption = new Adoption("A-4", "U-4", LocalDate.now());

        assertThrows(RuntimeException.class,
                () -> service.processAdoption(dog, adoption));

        assertTrue(connection.wasRolledBack());
        assertTrue(logDAO.findByAnimal("A-4").isEmpty());
        assertTrue(connection.getAutoCommit());
    }

    @Test
    void processAdoption_auditInsertFails_rollsBack() {
        TrackingAnimalDAO animalDAO = new TrackingAnimalDAO();
        AdoptionDAO adoptionDAO = new dao.InMemoryAdoptionDAO();
        StatusChangeLogDAO logDAO = new FailingStatusChangeLogDAO();
        FakeConnection connection = new FakeConnection();
        AdoptionService service =
                new AdoptionService(adoptionDAO, animalDAO, logDAO, connection);

        Dog dog = new Dog("A-5", "Boxer", LocalDate.now(),
                Animal.Status.READY_FOR_ADOPTION);
        animalDAO.save(dog);
        Adoption adoption = new Adoption("A-5", "U-5", LocalDate.now());

        assertThrows(RuntimeException.class,
                () -> service.processAdoption(dog, adoption));
        assertTrue(connection.wasRolledBack());
        assertTrue(connection.getAutoCommit());
    }

    @Test
    void processAdoption_mismatchedAnimalId_throwsException() {
        TrackingAnimalDAO animalDAO = new TrackingAnimalDAO();
        AdoptionDAO adoptionDAO = new dao.InMemoryAdoptionDAO();
        FakeConnection connection = new FakeConnection();
        AdoptionService service = new AdoptionService(adoptionDAO, animalDAO, connection);

        Dog dog = new Dog("A-6", "Boxer", LocalDate.now(),
                Animal.Status.READY_FOR_ADOPTION);
        animalDAO.save(dog);
        Adoption adoption = new Adoption("OTHER-ID", "U-6", LocalDate.now());

        assertThrows(IllegalArgumentException.class,
                () -> service.processAdoption(dog, adoption));
        assertFalse(connection.wasCommitted());
    }

    @Test
    void processAdoption_blankAdopterId_throwsException() {
        TrackingAnimalDAO animalDAO = new TrackingAnimalDAO();
        AdoptionDAO adoptionDAO = new dao.InMemoryAdoptionDAO();
        FakeConnection connection = new FakeConnection();
        AdoptionService service = new AdoptionService(adoptionDAO, animalDAO, connection);

        Dog dog = new Dog("A-7", "Boxer", LocalDate.now(),
                Animal.Status.READY_FOR_ADOPTION);
        animalDAO.save(dog);
        Adoption adoption = new Adoption("A-7", " ", LocalDate.now());

        assertThrows(IllegalArgumentException.class,
                () -> service.processAdoption(dog, adoption));
        assertFalse(connection.wasCommitted());
    }

    @Test
    void processAdoption_placementBeforeIntake_throwsException() {
        TrackingAnimalDAO animalDAO = new TrackingAnimalDAO();
        AdoptionDAO adoptionDAO = new dao.InMemoryAdoptionDAO();
        FakeConnection connection = new FakeConnection();
        AdoptionService service = new AdoptionService(adoptionDAO, animalDAO, connection);

        LocalDate intakeDate = LocalDate.of(2026, 2, 15);
        Dog dog = new Dog("A-8", "Boxer", intakeDate,
                Animal.Status.READY_FOR_ADOPTION);
        animalDAO.save(dog);
        Adoption adoption = new Adoption("A-8", "U-8", intakeDate.minusDays(1));

        assertThrows(IllegalArgumentException.class,
                () -> service.processAdoption(dog, adoption));
    }

    @Test
    void processAdoption_nullPlacementDate_throwsException() {
        TrackingAnimalDAO animalDAO = new TrackingAnimalDAO();
        AdoptionDAO adoptionDAO = new dao.InMemoryAdoptionDAO();
        FakeConnection connection = new FakeConnection();
        AdoptionService service = new AdoptionService(adoptionDAO, animalDAO, connection);

        Dog dog = new Dog("A-9", "Boxer", LocalDate.now(),
                Animal.Status.READY_FOR_ADOPTION);
        animalDAO.save(dog);
        Adoption adoption = new Adoption("A-9", "U-9", null);

        assertThrows(IllegalArgumentException.class,
                () -> service.processAdoption(dog, adoption));
    }

    @Test
    void processAdoption_notConfigured_throwsException() {
        AdoptionService service = new AdoptionService(new dao.InMemoryAdoptionDAO());
        Dog dog = new Dog("A-10", "Boxer", LocalDate.now(),
                Animal.Status.READY_FOR_ADOPTION);
        Adoption adoption = new Adoption("A-10", "U-10", LocalDate.now());

        assertThrows(IllegalStateException.class,
                () -> service.processAdoption(dog, adoption));
    }

    @Test
    void processAdoption_matchingFlow_withoutJdbcConnection_succeeds() {
        TrackingAnimalDAO animalDAO = new TrackingAnimalDAO();
        InMemoryAdopterDAO adopterDAO = new InMemoryAdopterDAO();
        AdoptionDAO adoptionDAO = new dao.InMemoryAdoptionDAO();
        InMemoryStatusChangeLogDAO logDAO = new InMemoryStatusChangeLogDAO();
        AdoptionService service =
                new AdoptionService(adoptionDAO, animalDAO, adopterDAO, logDAO, null);

        Dog dog = new Dog("A-11", "Labrador", LocalDate.now().minusDays(1),
                Animal.Status.READY_FOR_ADOPTION);
        animalDAO.save(dog);
        adopterDAO.save(new Adopter("U-11", "Lucia", "111", "Dog", "Labrador"));

        service.processAdoption("A-11", "U-11", LocalDate.now());

        assertEquals(Animal.Status.ADOPTED, dog.getStatus());
        assertEquals(1, logDAO.findByAnimal("A-11").size());
    }

    @Test
    void processAdoption_matchingFlow_incompatible_throwsException() {
        TrackingAnimalDAO animalDAO = new TrackingAnimalDAO();
        InMemoryAdopterDAO adopterDAO = new InMemoryAdopterDAO();
        AdoptionDAO adoptionDAO = new dao.InMemoryAdoptionDAO();
        InMemoryStatusChangeLogDAO logDAO = new InMemoryStatusChangeLogDAO();
        AdoptionService service =
                new AdoptionService(adoptionDAO, animalDAO, adopterDAO, logDAO, null);

        Dog dog = new Dog("A-12", "Labrador", LocalDate.now().minusDays(1),
                Animal.Status.READY_FOR_ADOPTION);
        animalDAO.save(dog);
        adopterDAO.save(new Adopter("U-12", "Mario", "999", "Cat", ""));

        assertThrows(IllegalArgumentException.class,
                () -> service.processAdoption("A-12", "U-12", LocalDate.now()));
    }
}
