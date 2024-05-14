import Employee from "../models/Employee.js";
import axios from 'axios';

export const createEmployee = async (req, res) => {
    try {
        const { employeeId, firstName, lastName, vacationDays, paidToDate, paidLastYear, payRate, payRateId } = req.body;

        // creating a new Employee object
        const employee = new Employee({
            employeeId,
            firstName,
            lastName,
            vacationDays,
            paidToDate,
            paidLastYear,
            payRate,
            payRateId
        });

        // saving the new employee
        const savedUser = await employee.save();
        axios.get('http://localhost:8080/api/ws/update')
                .then(function (response) {
                    console.log("success on update in middleware");
                  })
                .catch(function (error){
                    console.log("error on update in middleware")
                })
        return res.status(200).json({
            success: true, data: {
                _id: savedUser._id,
                employeeId: savedUser.employeeId,
                firstName: savedUser.firstName,
                lastName: savedUser.lastName,
                vacationDays: savedUser.vacationDays,
                paidToDate: savedUser.paidToDate,
                paidLastYear: savedUser.paidLastYear,
                payRate: savedUser.payRate,
                payRateId: savedUser.payRateId
            }
        });
    } catch (error) {
        console.error({success: true, data: error});
    }
};

export const getEmployee = async (req, res, next) => {
    const employee = await Employee.findById(req.params.employeeId);
    return res.json({ success: true, data: employee });
};

export const getEmployeeByFirstNameAndLastName = async (req, res, next) =>{
    const {firstName, lastName} = req.body;
    const query = {firstName, lastName}
    const employee = await Employee.findOne(query);
    return res.json({ success: true, data: employee });
}

export const getEmployees = async (req, res, next) => {
    const employees = await Employee.find();
    return res.json({ success: true, data: employees });
}
