require("dotenv").config();
let path = require("path");
let embedToken = require(__dirname + "/embedConfigService.js");
const utils = require(__dirname + "/utils.js");
const express = require("express");
const redis = require("redis");
const cookieParser = require("cookie-parser");
const validator = require("validator");
const { v4: uuidv4 } = require("uuid");
const bodyParser = require("body-parser");
const { getAllReports } = require("./embedConfigService");
const app = express();

const env = process.env.ENV;
const auth = env === "DEV" ? true : false;

// use this middleware to parse cookie data
app.use(cookieParser());

// Prepare server for Bootstrap, JQuery and PowerBI files
app.use(
  "/js",
  express.static(path.join(__dirname, "../node_modules/bootstrap/dist/js/"))
); // Redirect bootstrap JS
app.use(
  "/js",
  express.static(path.join(__dirname, "../node_modules/jquery/dist/"))
); // Redirect JS jQuery
app.use(
  "/js",
  express.static(path.join(__dirname, "../node_modules/powerbi-client/dist/"))
); // Redirect JS PowerBI
app.use(
  "/css",
  express.static(path.join(__dirname, "../node_modules/bootstrap/dist/css/"))
); // Redirect CSS bootstrap
app.use("/public", express.static(path.join(__dirname, "../public/"))); // Use custom JS and CSS files

// specify the port in .env created at root dir, if no .env exists => default to port 5300
const port = process.env.PORT || 5300;

// specify the port in .env created at root dir, if no .env exists => default to port 6379
const REDIS_PORT = process.env.REDIS_PORT || 6379;
const client = redis.createClient(REDIS_PORT);

app.use(bodyParser.json());

app.use(
  bodyParser.urlencoded({
    extended: true,
  })
);

app.post("/", function (req, res) {
  const userName = req.body.username;

  client.get("admin;" + userName, function (err, reply) {
    const PBESessionID = uuidv4();

    if (reply === req.body.password) {
      //expiry time = one day
      client.set(
        "PBE".concat(userName),
        PBESessionID,
        "EX",
        60 * 60 * 24,
        redis.print
      ); // => "Reply: OK"

      // set client cookie here
      res.cookie("PBESESSIONID", PBESessionID, {
        httpOnly: true,
      });

      res.cookie("USER", "PBE".concat(userName), {
        httpOnly: true,
        encode: (v) => v, // specify this option to turn off encoding url
      });
      res.sendFile(path.join(__dirname + "/../views/index.html"));
    } else {
      client.get("normal;" + userName, function (err, reply) {
        const PBESessionID = uuidv4();

        if (reply === req.body.password) {
          // expiry time = one day
          client.set(
            "PBE".concat(userName),
            PBESessionID,
            "EX",
            60 * 60 * 24,
            redis.print
          ); // => "Reply: OK"

          // set client cookie here
          res.cookie("PBESESSIONID", PBESessionID, {
            httpOnly: true,
          });

          res.cookie("USER", "PBE".concat(userName), {
            httpOnly: true,
            encode: (v) => v, // specify this option to turn off encoding url
          });
          res.sendFile(path.join(__dirname + "/../views/normalUser.html"));
        } else {
          res.sendFile(path.join(__dirname + "/../views/404.html"));
        }
      });
    }
  });
});

app.get("/", function (req, res) {
  client.get(req.cookies.USER, function (err, reply) {
    if (
      (reply !== undefined &&
        reply !== null &&
        req.cookies.PBESESSIONID !== undefined &&
        req.cookies.PBESESSIONID !== null &&
        validator.isUUID(req.cookies.PBESESSIONID, 4) &&
        validator.isUUID(reply, 4) &&
        reply === req.cookies.PBESESSIONID) ||
      auth
    ) {
      res.sendFile(path.join(__dirname + "/../views/index.html"));
    } else {
      res.sendFile(path.join(__dirname + "/../views/404.html"));
    }
  });
});

app.get("/createReport.html", function (req, res) {
  client.get(req.cookies.USER, function (err, reply) {
    if (
      (reply !== undefined &&
        reply !== null &&
        req.cookies.PBESESSIONID !== undefined &&
        req.cookies.PBESESSIONID !== null &&
        validator.isUUID(req.cookies.PBESESSIONID, 4) &&
        validator.isUUID(reply, 4) &&
        reply === req.cookies.PBESESSIONID) ||
      auth
    ) {
      res.sendFile(path.join(__dirname + "/../views/createReport.html"));
    } else {
      res.sendFile(path.join(__dirname + "/../views/404.html"));
    }
  });
});

app.get("/getAllReports", function (req, res) {
  // authenticate client
  client.get(req.cookies.USER, async function (err, reply) {
    if (
      (reply !== undefined &&
        reply !== null &&
        req.cookies.PBESESSIONID !== undefined &&
        req.cookies.PBESESSIONID !== null &&
        validator.isUUID(req.cookies.PBESESSIONID, 4) &&
        validator.isUUID(reply, 4) &&
        reply === req.cookies.PBESESSIONID) ||
      auth
    ) {
      let reportsRes = await getAllReports();
      res.status(reportsRes.status).send(reportsRes);
    } else {
      res.sendFile(path.join(__dirname + "/../views/404.html"));
    }
  });
});

// exposed REST APIs from which client can call, here client call to get the embed token, 2 APIs called client-server, server-powerBI
app.get("/getEmbedToken", function (req, res) {
  client.get(req.cookies.USER, async function (err, reply) {
    // 4 here is uuid version 4
    if (
      (reply !== undefined &&
        reply !== null &&
        req.cookies.PBESESSIONID !== undefined &&
        req.cookies.PBESESSIONID !== null &&
        validator.isUUID(req.cookies.PBESESSIONID, 4) &&
        validator.isUUID(reply, 4) &&
        reply === req.cookies.PBESESSIONID) ||
      auth
    ) {
      let queryData = req._parsedUrl.query;

      if (validator.isAscii(queryData)) {
        let reportName = queryData.split("&")[0].split("=")[1];
        reportName = decodeURIComponent(reportName);

        let reportId = await embedToken.configReportIdByReportName(reportName);

        configCheckResult = utils.validateConfig(reportId);
        if (configCheckResult) {
          return {
            status: 400,
            error: configCheckResult,
          };
        }

        let datasetId = await embedToken.configDatasetByReportName(reportName);

        let result = await embedToken.getEmbedInfo(reportId, datasetId);
        res.status(result.status).send(result);
      } else {
        res.sendFile(path.join(__dirname + "/../views/404.html"));
      }
    } else {
      res.sendFile(path.join(__dirname + "/../views/404.html"));
    }
  });
});

app.get("/deleteReport", function (req, res) {
  client.get(req.cookies.USER, async function (err, reply) {
    let queryData = req._parsedUrl.query;
    let reportName = queryData.split("&")[0].split("=")[1];
    reportName = decodeURIComponent(reportName);

    let reportId = await embedToken.configReportIdByReportName(reportName);
    let datasetID = await embedToken.getDatasetIDFromReportID(reportId);

    let resultData = await embedToken.deleteDatasetInGroups(datasetID);
    let result = await embedToken.deleteReportInGroups(reportId);
    res.status(result.status).send(result);
  });
});

app.listen(port, () => console.log(`Listening on port ${port}`));
