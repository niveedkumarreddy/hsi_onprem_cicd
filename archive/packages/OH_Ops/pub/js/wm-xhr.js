
window.wm = {

  invokeXhr: function(service, pipeline, callback) {

    var xhr = new XMLHttpRequest();
    xhr.open('GET', 'README.md');
    xhr.onload = function() {

      if (xhr.status === 200) {
        console.log("response: ", xhr.responseText);
        console.log(xhr);
      } else {
        console.log("error: ", xhr);
        console.log(xhr);
      }
    }
    xhr.send();
  }

