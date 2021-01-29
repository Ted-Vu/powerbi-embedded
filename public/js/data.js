function load() {
	var hostName = JSON.parse(data)[0].hostName;

    document.getElementById("icon").src = hostName +"/theme-resources/assets/admin/layout/img/logo-app.png";
    document.getElementById("redirect").href = hostName + "/portal/group/guest/momentum";
}