var COLORS = [
    {name:'TURQUOISE', hex:'#1ABC9C'},
    {name:'GREEN SEA', hex:'#16A085'},
    {name:'EMERALD', hex:'#2ECC71'},
    {name:'NEPHRITIS', hex:'#27AE60'},
    {name:'PETER RIVER', hex:'#3498DB'},
    {name:'BELIZE HOLE', hex:'#2980B9'},
    {name:'BAMETHYST', hex:'#9B59B6'},
    {name:'WISTERIA', hex:'#8E44AD'},
    {name:'WET ASPHALT', hex:'#34495E'},
    {name:'MIDNIGHT BLUE', hex:'#2C3E50'},
    {name:'SUN FLOWER', hex:'#F1C40F'},
    {name:'ORANGE', hex:'#F39C12'},
    {name:'CARROT', hex:'#E67E22'},
    {name:'PUMPKIN', hex:'#D35400'},
    {name:'ALIZARIN', hex:'#E74C3C'},
    {name:'POMEGRANATE', hex:'#C0392B'},
    {name:'CLOUDS', hex:'#ECF0F1'},
    {name:'SILVER', hex:'#BDC3C7'},
    {name:'CONCRETE', hex:'#95A5A6'},
    {name:'ASBESTOS', hex:'#7F8C8D'}
];

(function($){
    //open-close sidebar once burger button is clicked
    $('.sidebar-toggler').on('click', function () {
        var $this = $(this),
            $parent = $this.parent();
        $parent.toggleClass('hidden-sidebar');
        $this.toggleClass('active');
        $('main').toggleClass('hidden-sidebar');
    });
})(jQuery);

String.prototype.contains = function(str){
    return this.indexOf(str) != -1;
};

Array.prototype.contains = function(obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
};

toastr.options = {
    "closeButton": false,
    "debug": false,
    "newestOnTop": false,
    "progressBar": false,
    "positionClass": "toast-bottom-right",
    "preventDuplicates": false,
    "onclick": null,
    "showDuration": "300",
    "hideDuration": "1000",
    "timeOut": "5000",
    "extendedTimeOut": "1000",
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
};