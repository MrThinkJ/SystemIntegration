using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Data;
using System.Data.Entity;
using System.IO;
using System.Linq;
using System.Net;
using System.Runtime.Remoting.Contexts;
using System.Text;
using System.Text.Json;
using System.Text.Json.Nodes;
using System.Text.Json.Serialization;
using System.Web;
using System.Web.Helpers;
using System.Web.Http;
using System.Web.Mvc;
using System.Xml.Linq;
using HRWebApp.Models;

namespace HRWebApp.Controllers
{
    public class PersonalsApiController : Controller
    {
        private HRDB db = new HRDB();
        // GET: PersonalsApi
        public JsonResult Index()
        {
            var personals = db.Personals.Include(p => p.Benefit_Plans1).Include(p => p.Emergency_Contacts).Include(p => p.Employment);
            return Json(personals.ToList(), JsonRequestBehavior.AllowGet);
        }

        [System.Web.Http.HttpPost]
        public JsonResult Create()
        {
            string requestData = "";
            using (Stream iStream = Request.InputStream)
            {
                using (StreamReader reader = new StreamReader(iStream, Encoding.UTF8))
                {
                    requestData = reader.ReadToEnd();
                }
            }
            Personal personal = Newtonsoft.Json.JsonConvert.DeserializeObject<Personal>(requestData);
            var client = new WebClient();
            var mongoEmployee = new
            {
                firstName = personal.First_Name,
                lastName = personal.Last_Name
            };
            var json = Newtonsoft.Json.JsonConvert.SerializeObject(mongoEmployee);
            client.Headers.Add(HttpRequestHeader.ContentType, "application/json");
            var response = bool.Parse(client.UploadString("http://localhost:8082/api/mongo", json));
            if (!response)
                return Json("Failed", JsonRequestBehavior.AllowGet);
            var success = true;
            try
            {
                db.Personals.Add(personal);
                db.SaveChanges();
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
                success = false;
            }
            if (!success)
                bool.Parse(client.UploadString("http://localhost:8082/api/mongo", "DELETE", json));
            client.DownloadString("http://localhost:8082/api/ws/all");
            return Json("Success", JsonRequestBehavior.AllowGet);

        }
    }
}