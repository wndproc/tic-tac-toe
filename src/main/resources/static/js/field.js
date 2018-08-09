const fieldSize = 10;

$(document).ready(function () {
    createField();
});

function createField() {
    for (let row = 0; row < fieldSize; row++) {
        let rowId = `row_${row}`;
        $("#field").append(`<tr id='${rowId}'>`);
        for (let col = 0; col < fieldSize; col++) {
            let cellId = row * fieldSize + col;
            $(`#${rowId}`).append(`<td id="cell_${cellId}" class="tile free"></td>`);
        }
        $("#field").append("</tr>");
    }
}
