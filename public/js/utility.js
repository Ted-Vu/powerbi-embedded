var currentBtn = "view-report";

function changeBtn(newBtnState) {
  currentBtn = newBtnState;
}

function loadReport() {
  document.getElementById(currentBtn).click();
}
