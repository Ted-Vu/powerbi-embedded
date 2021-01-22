let models = window["powerbi-client"].models;
let reportContainer = $("#report-container").get(0);
let reportViewContainer = $("#report-view-container").get(0);

// setup sending cookie to server
$.ajaxSetup({
  xhrFields: {
    withCredentials: true,
  },
});

var reportsInWorkspace = [];
$.ajax({
  type: "GET",
  url: "/getAllReports",
  crossDomain: true,
  xhrFields: {
    withCredentials: true,
  },
  success: function (reportsRes) {
    for (reports of reportsRes.reports) {
      reportsInWorkspace.push(reports.name);
    }
    $("#reportName").empty();
    $.each(reportsInWorkspace, function (i, p) {
      $("#reportName").append($(`<option value=${p}></option>`).val(p).html(p));
    });
  },

  error: function (err) {
    window.location.href = "https://fcn.momentumsystems.com.au/";
  },
});

// Initialize iframe for embedding report
powerbi.bootstrap(reportContainer, { type: "report" });
$("#create-button").on("click", function (e) {
  e.preventDefault();
  window.open("/createReport.html", "_blank");
});
$("#delete-button").on("click", function (e) {
  e.preventDefault();
  let formName = document
    .getElementById("reportName")
    .value.split(" ")
    .join("");

  $.ajax({
    type: "GET",
    url: "/getEmbedToken",
    data: {
      formName: formName,
      mode: "DELETE",
    },
    dataType: "json",
    success: function (embedData) {
      // calling MS REST APIs to delete
      // repopulate dropdown
    },

    error: function (err) {
      alert("Your PBE Session expired you will be redirected");
      window.location.href = "https://fcn.momentumsystems.com.au/";
    },
  });
});

$("#view-button").on("click", function (e) {
  e.preventDefault();

  let formName = document
    .getElementById("reportName")
    .value.split(" ")
    .join("");

  $.ajax({
    type: "GET",
    url: "/getEmbedToken",
    data: {
      formName: formName,
      mode: "VIEW",
    },
    dataType: "json",
    success: function (embedData) {
      // Create a config object with type of the object, Embed details and Token Type
      // config object determines HOW and WHAT embedded looks like

      let reportLoadConfig = {
        type: "report",
        tokenType: models.TokenType.Embed,
        accessToken: embedData.accessToken,

        // Use other embed report config based on the requirement. We have used the first one for demo purpose
        embedUrl: embedData.embedUrl[0].embedUrl,
        permissions: models.Permissions.All /*gives maximum permissions*/,
        viewMode: models.ViewMode.View,
      };

      // Use the token expiry to regenerate Embed token for seamless end user experience
      // Refer https://aka.ms/RefreshEmbedToken
      tokenExpiry = embedData.expiry;

      // Embed Power BI report when Access token and Embed URL are available
      // calling embed() to do the embedding here with reportLoadConfig fully configured from calling the above APIs
      let report = powerbi.embed(reportContainer, reportLoadConfig);

      // Clear any other loaded handler events
      report.off("loaded");

      // Triggers when a report schema is successfully loaded
      report.on("loaded", function () {
        console.log("Report load successful");
      });

      // Clear any other rendered handler events
      report.off("rendered");

      // Triggers when a report is successfully embedded in UI
      report.on("rendered", function () {
        console.log("Report render successful");
      });

      // Clear any other error handler events
      report.off("error");

      // Handle embed errors
      report.on("error", function (event) {
        let errorMsg = event.detail;
        console.error(errorMsg);
        return;
      });
    },

    error: function (err) {
      alert("Your PBE Session expired you will be redirected");
      window.location.href = "https://fcn.momentumsystems.com.au/";
    },
  });
});

$("#edit-button").on("click", function (e) {
  e.preventDefault();

  let reportName = document
    .getElementById("reportName")
    .value.split(" ")
    .join("");

  $.ajax({
    type: "GET",
    url: "/getEmbedToken",
    data: {
      reportName: reportName,
      mode: "EDIT",
    },
    dataType: "json",
    success: function (embedData) {
      // Create a config object with type of the object, Embed details and Token Type
      // config object determines HOW and WHAT embedded looks like
      // ALGORITHMS
      // 1. User hits "report" button, handle onClick by making an AJAX request and load powerBI
      let reportLoadConfig = {
        type: "report",
        tokenType: models.TokenType.Embed,
        accessToken: embedData.accessToken,

        // Use other embed report config based on the requirement. We have used the first one for demo purpose
        embedUrl: embedData.embedUrl[0].embedUrl,
        permissions: models.Permissions.All /*gives maximum permissions*/,
        viewMode: models.ViewMode.Edit,
        // Enable this setting to remove gray shoulders from embedded report
        settings: {
          background: models.BackgroundType.Transparent,
        },
      };

      // Use the token expiry to regenerate Embed token for seamless end user experience
      // Refer https://aka.ms/RefreshEmbedToken
      tokenExpiry = embedData.expiry;

      // Embed Power BI report when Access token and Embed URL are available
      // calling embed() to do the embedding here with reportLoadConfig fully configured from calling the above APIs
      let report = powerbi.embed(reportContainer, reportLoadConfig);

      // Clear any other loaded handler events
      report.off("loaded");

      // Triggers when a report schema is successfully loaded
      report.on("loaded", function () {
        console.log("Report load successful");
      });

      // Clear any other rendered handler events
      report.off("rendered");

      // Triggers when a report is successfully embedded in UI
      report.on("rendered", function () {
        console.log("Report render successful");
      });

      // Clear any other error handler events
      report.off("error");

      // Handle embed errors
      report.on("error", function (event) {
        let errorMsg = event.detail;
        console.error(errorMsg);
        return;
      });
    },

    error: function (err) {
      alert("Your PBE Session expired you will be redirected");
      window.location.href = "https://fcn.momentumsystems.com.au/";
    },
  });
});
