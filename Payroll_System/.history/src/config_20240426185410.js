import { config } from "dotenv";
config();

export const MONGODB_URI =
  process.env.MONGODB_URI || "mongodb://localhost/apicompany";
export const PORT = process.env.PORT || 4000;
export const SECRET = "yoursecretkey";

export const ADMIN_EMAIL = process.env.ADMIN_EMAIL || "admin@localhost";
export const ADMIN_USERNAME = process.env.ADMIN_USERNAME || "admin";
export const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || "admin";

export const mssql = {
  user: "sa",
  password: "Matkhau28122003@",
  server: "localhost",
  database: "HRDB",
  port: 1433, // Default SQL Server port
};