let active1 = "headBox nav-link active";
let hidden1 = "headBox nav-link";
let active2 = "divBox tab-pane fade show active";
let hidden2 = "divBox tab-pane fade";

let mpb = document.getElementById("mybatis-plus-button");
let mb = document.getElementById("mybatis-button");

let mp = document.getElementById("mybatis-plus");
let m = document.getElementById("mybatis");

function showTag(obj) {
    if (obj === mpb) {
        showMbp();
        return;
    }
    showMb();
}

function showMbp() {
    mpb.className = active1;
    mb.className = hidden1;

    mp.className = active2;
    m.className = hidden2;
}

function showMb() {
    mpb.className = hidden1;
    mb.className = active1;

    mp.className = hidden2;
    m.className = active2;
}