using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Security.Policy;
using System.Text;
using System.Text.Json;
using System.Web;
using System.Web.Configuration;
using System.Web.Mvc;
using HRWebApp.Models;

namespace HRWebApp.Controllers
{
    public class PersonalsController : Controller
    {
        private HRDB db = new HRDB();

        // GET: Personals
        public ActionResult Index()
        {
            var personals = db.Personals.Include(p => p.Benefit_Plans1).Include(p => p.Emergency_Contacts).Include(p => p.Employment);
            return View(personals.ToList());
        }

        // GET: Personals/Details/5
        public ActionResult Details(decimal id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Personal personal = db.Personals.Find(id);
            if (personal == null)
            {
                return HttpNotFound();
            }
            return View(personal);
        }

        // GET: Personals/Create
        public ActionResult Create()
        {
            ViewBag.Benefit_Plans = new SelectList(db.Benefit_Plans, "Benefit_Plan_ID", "Plan_Name");
            ViewBag.Employee_ID = new SelectList(db.Emergency_Contacts, "Employee_ID", "Emergency_Contact_Name");
            ViewBag.Employee_ID = new SelectList(db.Employments, "Employee_ID", "Employment_Status");
            return View();
        }

        // POST: Personals/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "Employee_ID,First_Name,Last_Name,Middle_Initial,Address1,Address2,City,State,Zip,Email,Phone_Number,Social_Security_Number,Drivers_License,Marital_Status,Gender,Shareholder_Status,Benefit_Plans,Ethnicity")] Personal personal)
        {
            if (ModelState.IsValid)
            {
                var client = new WebClient();
                var json = Newtonsoft.Json.JsonConvert.SerializeObject(personal);
                client.Headers.Add(HttpRequestHeader.ContentType, "application/json");
                var response = client.UploadString("http://localhost:8081/api/sql", json);
            }
            var personals = db.Personals.Include(p => p.Benefit_Plans1).Include(p => p.Emergency_Contacts).Include(p => p.Employment);
            return RedirectToAction("Index");
        }

        // GET: Personals/Edit/5
        public ActionResult Edit(decimal id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Personal personal = db.Personals.Find(id);
            if (personal == null)
            {
                return HttpNotFound();
            }
            ViewBag.Benefit_Plans = new SelectList(db.Benefit_Plans, "Benefit_Plan_ID", "Plan_Name", personal.Benefit_Plans);
            ViewBag.Employee_ID = new SelectList(db.Emergency_Contacts, "Employee_ID", "Emergency_Contact_Name", personal.Employee_ID);
            ViewBag.Employee_ID = new SelectList(db.Employments, "Employee_ID", "Employment_Status", personal.Employee_ID);
            return View(personal);
        }

        // POST: Personals/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "Employee_ID,First_Name,Last_Name,Middle_Initial,Address1,Address2,City,State,Zip,Email,Phone_Number,Social_Security_Number,Drivers_License,Marital_Status,Gender,Shareholder_Status,Benefit_Plans,Ethnicity")] Personal personal)
        {
            if (ModelState.IsValid)
            {
                Personal personal1 = db.Personals.Find(personal.Employee_ID);
                if (personal1 != null)
                {
                    db.Entry(personal1).State = EntityState.Detached;
                }
                db.Entry(personal).State = EntityState.Modified;
                db.SaveChanges();
                var payload = new
                {
                    firstNameBefore = personal1.First_Name,
                    lastNameBefore = personal1.Last_Name,
                    firstNameAfter = personal.First_Name,
                    lastNameAfter = personal.Last_Name
                };

                var json = Newtonsoft.Json.JsonConvert.SerializeObject(payload);
                var client = new WebClient();
                client.Headers.Add(HttpRequestHeader.ContentType, "application/json");
                client.UploadString("http://localhost:8082/api/mongo/createOrUpdate", "PUT", json);
                client.DownloadString("http://localhost:8082/api/ws/all");
                return RedirectToAction("Index");
            }

            return RedirectToAction("Index");
        }

        // GET: Personals/Delete/5
        public ActionResult Delete(decimal id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Personal personal = db.Personals.Find(id);
            if (personal == null)
            {
                return HttpNotFound();
            }
            return View(personal);
        }

        // POST: Personals/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(decimal id)
        {
            Personal personal = db.Personals.Find(id);
            var payload = new {
                firstName = personal.First_Name,
                lastName = personal.Last_Name
            };
            var json = Newtonsoft.Json.JsonConvert.SerializeObject(payload);
            var client = new WebClient();
            client.Headers.Add(HttpRequestHeader.ContentType, "application/json");
            client.UploadString(address:"http://localhost:8081/api/sql", method:"DELETE", data:json);
            return RedirectToAction("Index");
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }
    }
}
