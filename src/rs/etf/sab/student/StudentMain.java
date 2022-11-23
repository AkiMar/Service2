package rs.etf.sab.student;

import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;


public class StudentMain {

    public static void main(String[] args) {
        AddressOperations addressOperations = new ma180130_address(); // Change this to your implementation.
        CityOperations cityOperations = new ma180130_city(); // Do it for all classes.
        CourierOperations courierOperations = new ma180130_courier(); // e.g. = new MyDistrictOperations();
        CourierRequestOperation courierRequestOperation = new ma180130_courierReq();
        DriveOperation driveOperation = new ma180130_drive();
        GeneralOperations generalOperations = new ma180130_general();
        PackageOperations packageOperations = new ma180130_package();
        StockroomOperations stockroomOperations = new ma180130_stockroom();
        UserOperations userOperations = new ma180130_user();
        VehicleOperations vehicleOperations = new ma180130_vehicle();


        TestHandler.createInstance(
                addressOperations,
                cityOperations,
                courierOperations,
                courierRequestOperation,
                driveOperation,
                generalOperations,
                packageOperations,
                stockroomOperations,
                userOperations,
                vehicleOperations);

        TestRunner.runTests();
        
        
        
    }
    
}
