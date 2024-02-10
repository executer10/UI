let HTML_ONOFF = document.querySelector('.HTML');
handleBoxOnOff(HTML_ONOFF);

let CSS_ONOFF = document.querySelector('.CSS');
handleBoxOnOff(CSS_ONOFF);

let JS_ONOFF = document.querySelector('.JS');
handleBoxOnOff(JS_ONOFF);

let JAVA_ONOFF = document.querySelector('.JAVA');
handleBoxOnOff(JAVA_ONOFF);

let Oracle_ONOFF = document.querySelector('.Oracle');
handleBoxOnOff(Oracle_ONOFF);

let JSP_ONOFF = document.querySelector('.JSP');
handleBoxOnOff(JSP_ONOFF);

let Spring_ONOFF = document.querySelector('.Spring');
handleBoxOnOff(Spring_ONOFF);

let Python_ONOFF = document.querySelector('.Python');
handleBoxOnOff(Python_ONOFF);


function handleBoxOnOff(element){
    element.onmouseover =function(){
        this.style.width = '300px';
        this.style.height = '300px';
    }
    element.onmouseout =function(){
        this.style.width = '200px';
        this.style.height = '200px';
    }
}
