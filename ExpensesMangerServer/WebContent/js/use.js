/**
 * Created by Jiminy Cricket on 5/23/2016.
 */

/**
 * util namespace
 */
window.util = {};


/**
 * initialization of util object's variables
 */
util.sessionUser = {
    username : '',
    userid : ''
};
util.categoryList = [];
util.categoryArrayVolume = [];
util.budgets = [];
util.monthsExpenses = [];
util.monthsNames = [];


/**
 * pageinit events triggered on the page being initialized, after initialization occurs. (once)
 */
$( document ).on( "pageinit", "#login", function() {
    $("#loginBtn").on("click", util.loginBtnOnClickEvent);
});
$( document ).on( "pageinit", "#home", function() {
    $("#welcome_title").text("Welcome, " + util.sessionUser.username);
    util.getCategoryListFromServer();
});
$( document ).on( "pageinit", "#newItemPopup", function(){
    $("#new_item_submit_btn").on( "click", util.addNewItemAction);
});
$( document ).on( "pageinit", "#registration_page", function(){
    $("#registration_btn").on( "click", util.registrationBtnOnClickEvent);
});
$( document ).on( "pageinit", "#expensesListPage", function (){
    util.getLastTwelveMonthsTotals();
    $( document ).on("click", ".delete-item", util.deleteItemBtnsClickEevnt);
    $( document ).on("click", ".item-a-element", util.itemAElementClickEvent);
    $( document ).on( "swiperight", "#expensesListPage", util.expensesListPageSwipeRightEvent);
});
$( document ).on( "pageinit", "#settingPage", function (){
    $( document ).on("click", ".delete_category_btn", util.deleteCategoryOnClickEvent);
    $( document ).on("click", ".add-category-btn", util.addCategoryBtnOnClickEvent);
    $( document ).on( "swiperight", "#settingPage", util.settingPageOnSwipeRightEvent);
});

/**
 *These "pagebeforeshow" events are used since some of the injected html element are being dynamically changed through application run
 */
$( document ).on( "pagebeforeshow", "#newItemPopup", function(){
    util.getCategoryListForNewItemForm();
});
$( document ).on( "pagebeforeshow", "#expensesListPage", function(){
    util.setCategoryFilterHeader();
    util.getExpensesListToUl();
});
$( document ).on( "pagebeforeshow", "#settingPage", function(){
    util.updateCategoryListViewInSettingPage();
    if ($('#category_listview').children().length === 1){
        popupMessage('Please add some categories first', 'emptyCategoryPopup');
    }
});

/**
 * Graphs will be added on pageshow event of expensesListPage
 */
$( document ).on( "pageshow", "#expensesListPage", function(){
    util.buildPieGraph();
    util.createMonthsChart();
});
$( document ).on( "pageshow", "#newItemPopup", function(){
    var categorySelect = $('#category-select-new-item-form');
    if (categorySelect.children().length === 0){
        $.mobile.pageContainer.pagecontainer("change", "#settingPage");
    }
});
/**
 * The jquery mobile's widget supply a custom filtering options using the filter callback function
 * this function was manipulated in way that it will be filtered using a generated sting of date scope and categories
 * it was also used to build a global array object that will be used by the graphs in the same page
 * @param index
 * @param searchValue
 * @returns {boolean}
 */
$.mobile.filterable.prototype.options.filterCallback = function( index, searchValue ) {

    var today = new Date();
    var expenseDate = new Date($(this).find(".li-item-date").text());
    var startDate;
    var itemCost = parseInt($(this).find(".li-item-cost").text());
    var itemCategory = $(this).find(".li-item-category").text().toLowerCase();
    var categoryArray = [];
    var dateScope = searchValue[0];
    var ulLength = $("#expenses_listview").find("li").length;

    //split the String to two parts, the first part will be the date scope, second part is the category string
    searchValue = searchValue.split( '&' );

    //initializing the "startDate variable" according to choosing
    switch(dateScope){
        case 'today':
            startDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 1);
            break;
        case 'thisweek':
            startDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 7);
            break;
        case 'thismonth':
            startDate = new Date(today.getFullYear(), today.getMonth() - 1 , today.getDate());
            break;
        case 'threemonths':
            startDate = new Date(today.getFullYear(), today.getMonth() - 3 , today.getDate());
            break;
        default:
            startDate = new Date(1970,1,1);
    }
    //if categories exist in string
    if(searchValue[1]) {
        //split them into array
        categoryArray = searchValue[1].split(',');
    }

    //adding the correct filtered records to the array, that later being used by the graphs
    util.addItemCostToCategoryArrayVolumeBasedOnDateScope(startDate, today, itemCategory, itemCost, expenseDate, index, ulLength);

    //check if item's date is within the date range
    if (expenseDate <= today && expenseDate >= startDate){
        //if no categories were selected consider as if all of them were selected
        if (categoryArray.length === 0) {
            //false = do not hide the li item
            return false;
        }

        //go through selected categories array
        for (var idx = 0 ; idx < categoryArray.length ; idx++ ){
            //if the li elemnt's category matches a selected category
            if ( itemCategory === categoryArray[idx].toLowerCase())
                //false = do not hide the li item
                return false;
        }
    }
    return !!searchValue;
};

/**
 * when the login button pressed check if there're empty feilds
 *   and call the login request function
 */
util.loginBtnOnClickEvent = function (){
    var vusername = $('#loginUsername').val();
    var vpassword = $('#loginPassword').val();
    if(vusername=='' && vpassword=='')
    {
        popupMessage('Please fill out the form', 'emptyLoginForm');
    }
    else if(vusername=='' && vpassword!==''){popupMessage('Username field is required', 'emptyUsernamePopup');}
    else if(vpassword=='' && vusername!==''){popupMessage('Password field is required', 'emptyPasswordPopup');}
    else{ util.loginRequest(vusername, vpassword); }
};

/**
 * when when the registration button is clicked check if there're empty feilds
 *   and call for registerAction function
 */
util.registrationBtnOnClickEvent = function (){
    var vusername = $('#reg_username').val();
    var vpassword = $('#reg_password').val();
    if(vusername=='' && vpassword=='')
    {
        popupMessage('Please fill out the form', 'emptyRegFormPopup');
    }
    else if(vusername=='' && vpassword!==''){popupMessage('Username field is required', 'emptyRegUsernamePopup')}
    else if(vpassword=='' && vusername!==''){popupMessage('Password field is required', 'emptyRegPasswordPopup')}
    else{ util.registerAction(vusername, vpassword); }
};

/**
 * when one of the delete buttons of the ul list is clicked
 * handle the removing of the item from the ul element and call for deleteItemAction to update the server
 */
util.deleteItemBtnsClickEevnt = function () {
    var listitem = $(this).parent();
    var itemid = listitem.attr('itemid');
    console.log(itemid);
    util.deleteItemAction(itemid);
    //in order to update filters and graphs we're zeroing the util.categoryArrayVolume used by the pie graph
    $('#input-hidden-filter-string').change();
    console.log('itemid: ' + itemid + ' was removed');
    listitem.remove();
    $('#expenses_listview').listview().listview('refresh');
};

/**
 * when the an li element is clicked within the expenses list page
 * it will be replaced with an update form
 */
util.itemAElementClickEvent = function (){
    var liItem = $(this).parent();
    util.updateListItemToForm(liItem);

    //the new update item submit button that was dynamically created will be binned with onClick event
    // that calls to updateItemAction function
    $("#update_item_submit_btn").click(function() {
        util.updateItemAction($(this));
    });
};

/**
 * when swiping right on the expenses page open the manu panel
 * @param e
 */
util.expensesListPageSwipeRightEvent = function (e) {
    // We check if there is no open panel on the page because otherwise
    // a swipe to close the left panel would also open the right panel (and v.v.).
    // We do this by checking the data that the framework stores on the page element (panel: open).
    if ( $( ".ui-page-active" ).jqmData( "panel" ) !== "open" ) {
        if ( e.type === "swiperight" ) {
            $( "#left-panel-ev" ).panel( "open" );
        }

    }

};

/**
 * when clicking the delete button in the category list
 * call for deleteCategoryAction with the correct parameters
 */
util.deleteCategoryOnClickEvent = function () {
    var listItem = $(this).parent();
    var categoryid = listItem.attr("categoryid");
    var categoryName = listItem.find(".li_category_name").text();
    console.log(categoryid + " " + categoryName);
    util.deleteCategoryAction(categoryid, categoryName, listItem);
};

/**
 * when clicking the add category button, update the the top li element to a form element
 * and add an on click event handler to the submit button
 */
util.addCategoryBtnOnClickEvent = function () {
    util.updateAddCategoryLiToForm($(this));
    $('.submit_new_category_btn').click(function() {
        var form = $(document).find('#add_category_form');
        util.addCategoryAction(form);
    });
};

/**
 * when swiping right on the setting page opens the menu panel
 * @param e
 */
util.settingPageOnSwipeRightEvent = function ( e ) {
    // We check if there is no open panel on the page because otherwise
    // a swipe to close the left panel would also open the right panel (and v.v.).
    // We do this by checking the data that the framework stores on the page element (panel: open).
    if ( $( '.ui-page-active' ).jqmData( 'panel' ) !== 'open' ) {
        if ( e.type === 'swiperight' ) {
            $( '#left-panel-sp' ).panel( 'open' );
        }

    }

};

/**
 * request server a new item addition with the form parameters and user ID
 * on success response reset the form, and change page to expenses list page
 * check if the new item addition to the server was responded with '1' - a budget deviation
 * and display a proper message if it does
 */
util.addNewItemAction = function (){
    $.ajax({
        type: 'POST',
        url: '/ExpensesMangerServer/servletcontroller/addnewitemaction',
        data: $('#new_item_form').serialize() + '&userid=' + util.sessionUser.userid,
        success: function(data){
            console.log( 'item was added successfully' );
            $('#new_item_form').trigger("reset");
            $.mobile.pageContainer.pagecontainer('change', '#expensesListPage');
            if (data === '1') {
                var category = $('#category-select-new-item-form').text();
                var budgetDeviationWarning = $('#budget-deviation-warning');
                budgetDeviationWarning.html('<p><h2>Budget Deviation Detected</h2><p>');
                budgetDeviationWarning.popup('open');
            }
        },
        error: util.errorHandling,
        dataType: "text"
    });
};

/**
 * function for handling an error that was received upon server request
 * display an error details message
 * @param xhr
 * @param status
 * @param error
 */
util.errorHandling = function (xhr,status,error){
    if(xhr.status != 409) {
        popupMessage('error: ' + error + '\nstatus: ' + status + '\nxhr status: ' + xhr.status, 'errorMessage');
    }
};

/**
 * send a login request to the server with username and password parameters
 * receive a user ID if login was successful or '0' if it wasn't
 * will display a proper message if unsuccessful or navigate to homepage if successful
 * @param vusername
 * @param vpassword
 */
util.loginRequest = function (vusername, vpassword){
    $.ajax({
        type: "POST",
        url: "/ExpensesMangerServer/servletcontroller/loginaction",
        data: {
            username:vusername,
            password:vpassword
        },
        success: function(result){
            var recevedUserId = parseInt(result);
            console.log("userid recieved: " + recevedUserId);
            if (recevedUserId > 0){
                util.sessionUser.username = vusername;
                util.sessionUser.userid = recevedUserId;
                $.mobile.pageContainer.pagecontainer("change", "#home");
            }
            else if (recevedUserId == 0){
                popupMessage('incorrect password', 'incorrectPasswordPopup');
            }
            else{
                popupMessage("username doesn't exist", 'nonExistsUsernamePopup');
            }
        },
        error: util.errorHandling
    });
};

/**
 * send a register user request to the server
 * this was using an xml response
 * will navigate to homepage after successful response
 * @param vusername
 * @param vpassword
 */
util.registerAction = function (vusername, vpassword){
    $.ajax({
        type: "POST",
        url: "/ExpensesMangerServer/servletcontroller/registeraction",
        data: {
            username:vusername,
            password:vpassword
        },
        success: function(result){
            var xml_node = $('user',result);
            util.sessionUser.userid = xml_node.find('id').text();
            util.sessionUser.username = xml_node.find('username').text();
            console.log( 'id:' + util.sessionUser.userid);
            console.log( 'username:' + util.sessionUser.username);
            $.mobile.pageContainer.pagecontainer('change', '#home');
        },
        error: util.errorHandling,
        dataType: "xml"
    });
};

/**
 * send an item update request to the server with the update item form values
 * will call the getExpensesListToUl function upon succesful response
 * @param submit_btn
 */
util.updateItemAction = function (submit_btn){
    var form = submit_btn.closest("form");
    $.ajax({
        type: "POST",
        url: "/ExpensesMangerServer/servletcontroller/updateitemaction",
        data: form.serialize() + "&userid=" + util.sessionUser.userid,
        success: function(result){
            console.log( "item was updated\n" + result.id);
            util.getExpensesListToUl();
        },
        error: util.errorHandling,
        dataType: "json"
    });
};

/**
 * will send a request to the server to get the expenses list for the session user
 * response will be in json
 * call updateExpensesListViewWithJson to update an html ul element accordingly
 */
util.getExpensesListToUl = function (){
    $.ajax({
        type: "POST",
        url: "/ExpensesMangerServer/servletcontroller/getexpenseslist",
        data: { userid: util.sessionUser.userid },
        success: function(result){ util.updateExpensesListViewWithJson(result); },
        error: util.errorHandling,
        dataType: "json"
    });
};

/**
 * using a json object, update the expenses_listview element with list of items
 * @param result
 */
util.updateExpensesListViewWithJson = function (result){
    var expensesListview = $('#expenses_listview');
    expensesListview.empty();
		        $.each( result.expenses , function(i, item) {
                    expensesListview.append('<li itemid="' + item.id + '">' +
                        '<a href="#" class="item-a-element">' +
                        '<h3 class="li-item-name">' + item.name + '</h3>' +
                        '<p class="li-item-comment">' + item.comment + '</p>' +
                        '<p class="li-item-cost">' + item.cost + '</p>' +
                        '<p class="li-item-category">' + item.category + '</p>' +
                        '<p class="li-item-date">' + item.date + '</p></a>'+
                        '<a href="#" class="delete-item">Delete</a></li>');
                    
		        });
    expensesListview.listview().listview('refresh');
};

/**
 * will replace the selected li element with an item update form, keeping the original values in fields  
 * @param liItem
 */
util.updateListItemToForm = function (liItem){
	var itemName = liItem.find(".li-item-name").text();
    var itemcomment = liItem.find(".li-item-comment").text();
    var itemCost = liItem.find(".li-item-cost").text();
    var itemcategory = liItem.find(".li-item-category").text();
    var itemdate = liItem.find(".li-item-date").text();
    var itemid = liItem.attr("itemid");
    liItem.empty();
    liItem.html('<a href="#">' +
                        '<form id="update_item_form">' +
                            '<input type="hidden" name="itemid" value="' + itemid +'">' +
                            '<input type="text" name="name" value="' + itemName + '">' +
                            '<input type="text" name="comment" value="' + itemcomment + '">' +
                            '<input type="number" name="cost" value="' + itemCost + '">' +
                            '<input type="text" name="category" value="' + itemcategory + '">' +
                            '<input type="text" name="date" value="' + itemdate + '">' +
                            '<input type="submit" data-inline="true" data-mini="true" id="update_item_submit_btn" class="ui-btn" value="Update">' +
                        '</form>' +
                    '</a>'+
                    '<a href="#" class="delete-item">Delete</a>'
                );
    liItem.enhanceWithin();
};

/**
 * will send a delete item request to the server with the item's ID
 * @param itemid
 */
util.deleteItemAction = function (itemid){
    $.ajax({
        type: "POST",
        url: "/ExpensesMangerServer/servletcontroller/deleteitemaction",
        data: { itemid:itemid },
        success: function(result){ console.log(result); },
        error: util.errorHandling
    });
};

/**
 * will request the server for the category list of the current session user
 * will call setCategoryList function upon success
 */
util.getCategoryListFromServer = function () {
    $.ajax({
        type: "POST",
        url: "/ExpensesMangerServer/servletcontroller/getcategorieslist",
        data: {
            userid: util.sessionUser.userid
        },
        success: function(result){
            util.setCategoryList(result);
        },
        error: util.errorHandling,
        dataType: "json"
    });
};

/**
 * will build the category list view widget in the setting page using the global categoryList array object 
 */
util.updateCategoryListViewInSettingPage = function (){
    var categoryListview = $('#category_listview');
    categoryListview.empty();
    categoryListview.append('<li data-theme="a" class="add-category-btn"><a href="#">Add New Category</a></li>');
     $.each( util.categoryList , function(i, category) {
         categoryListview.append('<li categoryid="' + category.id + '"><a href="#" class="category_a_element">' +
             '<h3 class="li_category_name">' + category.name + '</h3>' +
             '<p class="li_category_budgetlimit">Monthly Budget: ' + category.budgetlimit + '</p>' +
             '</a>'+
             '<a href="#" class="delete_category_btn">Delete</a></li>');

     });
    categoryListview.listview().listview('refresh');
 };

/**
 * will request the server for deleting a category
 * when success response, remove the matched li element from the listview
 * and remove the category from the global categoryList element
 * popup a message if sever respond with 409 status meaning a category still in use and can't be deleted
 * @param categoryid
 * @param categoryName
 * @param listItem
 */
util.deleteCategoryAction = function (categoryid, categoryName, listItem){
    $.ajax({
        type: "POST",
        url: "/ExpensesMangerServer/servletcontroller/deletecategoryaction",
        data: { categoryid:categoryid,
                categoryname:categoryName,
                userid:util.sessionUser.userid
        },
        success: function(){
            console.log('categoryid: ' + categoryid + ' was removed');
            listItem.remove();
            $.each( util.categoryList , function(i, category) {
                if (category.id) {
                    if (category.id == categoryid) {
                        util.categoryList.splice(i, 1); //remove category from list
                    }
                }
            });

        },
        error: util.errorHandling,
        statusCode: {
            409: function () {
                $('#popupMessage').text('This category is currently still in use\nDelete expenses items first').popup( "open" );
            }
        }
    });
};

/**
 * will replace the li element's contents with a form in order to add new category
 * @param liItem
 */
util.updateAddCategoryLiToForm = function (liItem){
    liItem.attr("class", "li-category-form");
    liItem.attr("data-icon", "plus");
    liItem.empty();
    liItem.html('<a href="#">' +
        '<form id="add_category_form">' +
        '<input type="text" name="name" id="category_form_name" placeholder="Category Name">' +
        '<input type="number" name="budgetlimit" id="category_form_budgetlimit" placeholder="Set Budget Limit">' +
        '</form>' +
        '</a>'+
        '<a href="#" class="submit_new_category_btn" style="background: darkcyan">Add</a>'
    );
    $('#category_listview').listview('refresh').trigger("create");
};

/**
 * request the server for adding a new category
 *   upon success, add the new category to the global categoryList object
 *   and call updateCategoryListViewInSettingPage function for updating the listview widget
 *   a 409 status indicates that a category with same name already exists therefore, will not process the new category
 * @param form
 */
util.addCategoryAction = function (form){
    $.ajax({
        type: "POST",
        url: "/ExpensesMangerServer/servletcontroller/addcategoryaction",
        data: form.serialize() + "&userid=" + util.sessionUser.userid,
        success: function(result){
            console.log( "category was created:\n" + result);
            util.categoryList.push(result);
            util.updateCategoryListViewInSettingPage();
        },
        error: util.errorHandling,
        statusCode: {
            409: function () {
                $("#popupMessage").text("Category already exists").popup( "open" );
            }
        },
        dataType: "json"
    });
};

/**
 * request the server for the category list in order to update it in the "new item form"
 * upon success will call the updateCategoryListForNewItemForm to build the form
 */
util.getCategoryListForNewItemForm = function (){
    $.ajax({
        type: "POST",
        url: "/ExpensesMangerServer/servletcontroller/getcategorieslist",
        data: {
            userid: util.sessionUser.userid
        },
        success: function(result){
            util.updateCategoryListForNewItemForm(result);
        },
        error: util.errorHandling,
        dataType: "json"
    });
};

/**
 * will update the category-select element of the new item form with the list of categories
 * @param result
 */
util.updateCategoryListForNewItemForm = function (result){
    var categorySelectClass = $('.category-select');
    categorySelectClass.empty();
    $.each( result.categories , function(i, category) {
        $('.category-select').append('<option value="' + category.name + '">' + category.name + '</option>')
    });
    categorySelectClass.enhanceWithin();
};

/**
 * will set the global category list object with categories information of a json object received
 * @param jsonObject
 */
util.setCategoryList = function (jsonObject){
    util.categoryList.length = 0;
    $.each( jsonObject.categories , function(i, category){
        util.categoryList.push(category);
    });
    console.log(util.categoryList);
};

/**
 * build the category filter header in the expenses list page
 *   with categories information from the global categoryList Object
 *   will also add onChange events to categories checkboxes and datescope radio buttons
 */
util.setCategoryFilterHeader = function (){
    var categoryFilterForm = $("#category-filter-form");
    categoryFilterForm.empty();
    var content = '<fieldset data-role="controlgroup" data-type="horizontal" data-mini="true">';
    for( var i=0; i<util.categoryList.length ; i++){
    	content += '<input type="checkbox" name="'+ util.categoryList[i].name +'" id="checkbox-'+ util.categoryList[i].name +'" class="checkbox-category" checked="checked">'+
            '<label for="checkbox-'+ util.categoryList[i].name +'">'+ util.categoryList[i].name +'</label>';
    }
    content += '</fieldset>';
    categoryFilterForm.append(content).enhanceWithin();

    $( '.checkbox-category' ).on( "change",function() {
        util.generateFilerStringAndTriggerInputChange();
    });

    $( '#date-scope-form' ).on( "change",function() {
        util.generateFilerStringAndTriggerInputChange();
    });
};

/**
 * will generate a string used by the filterable widget
 *   the string is assembled from the chosen date radio button and categories checkboxes
 *   this string will be assigned to the input-hidden-filter-string input element
 *   which is the filter source of the widget
 *   a change event is triggered, telling the filter widget to take action and filter the listview
 */
util.generateFilerStringAndTriggerInputChange = function (){
    var stringFilter = $('#date-scope-form').serialize().replace("radio-choice-date-scope=", "") + '&';
    stringFilter += $('#category-filter-form').serialize().replace(/=on/g,"").replace(/&/g,",");
    console.log(stringFilter);
    $("#input-hidden-filter-string").val(stringFilter).change();
};

/**
 * use jqplot to build the Pie graph
 * this graph is build relatively to the date scope of choice
 */
util.buildPieGraph = function (){

	//if the pie graph already exists delete it
	if(util.pieGraph){
		util.pieGraph.destroy();
	}

    //case the category array is empty create a dummy first element
    if(util.categoryArrayVolume.length === 0){
        util.categoryArrayVolume.push(['',0]);
    }

    util.pieGraph = jQuery.jqplot('piechart', [util.categoryArrayVolume],
        {
            title: ' ',
            seriesDefaults: {
                shadow: false,
                renderer: jQuery.jqplot.PieRenderer,
                rendererOptions: { padding: 2, sliceMargin: 2, showDataLabels: true }
            },
            legend: {
                show: true,
                location: 'e',
                renderer: $.jqplot.EnhancedPieLegendRenderer,
                rendererOptions: {
                    numberColumns: 1
                }
            },
            grid: {
            	 background: '#111111'
                }
            
        }
    );
};

/**
 * request the server for last twelve month information of expenses
 * on success will call sortMonthsArrays function
 */
util.getLastTwelveMonthsTotals = function () {
    $.ajax({
        type: "POST",
        url: "/ExpensesMangerServer/servletcontroller/getlasttwelvemonths",
        data: {
            userid: util.sessionUser.userid
        },
        dataType:"json",
        success: function(data) {
            util.sortMonthsArrays(data);

        },
        error: util.errorHandling
    });
};

/**
 * will sort the data in the global array objects: budgets, monthsExpenses,monthsNames
 *   later used by the graphs
 * @param data
 */
util.sortMonthsArrays = function (data){
    var monthNames = [];
    monthNames[0] = "";
    monthNames[1] = "Jan";
    monthNames[2] = "Feb";
    monthNames[3] = "Mar";
    monthNames[4] = "Apr";
    monthNames[5] = "May";
    monthNames[6] = "Jun";
    monthNames[7] = "Jul";
    monthNames[8] = "Aug";
    monthNames[9] = "Sep";
    monthNames[10] = "Oct";
    monthNames[11] = "Nov";
    monthNames[12] = "Dec";
    $.each(data.months, function (i, month) {
        var monthName = monthNames[month.month] + ' ' + month.year;
        util.budgets.push([i, month.totalbudgetsLimit]);
        util.monthsExpenses.push([i, month.expense]);
        util.monthsNames.push(monthName);
    });

    //case the budgets array is empty create a dummy first element
    if(util.budgets.length === 0){
        util.budgets.push(['',0]);
    }
    //case the util.monthsExpenses array is empty create a dummy first element
    if(util.monthsExpenses.length === 0){
        util.monthsExpenses.push(['',0]);
    }
};

/**
 * use jqplot to build the monthsGraph - showing total expenses sum for each month and total budgets limit
 */
util.createMonthsChart = function () {
    if(util.monthsChart){
        util.monthsChart.destroy();
    }
    util.monthsChart = $.jqplot("monthsChart", [util.monthsExpenses, util.budgets], {
    	
        // Turns on animatino for all series in this plot.
        animate: true,
        // Will animate plot on calls to plot1.replot({resetAxes:true})
        animateReplot: true,

        series: [
            {
                pointLabels: {
                    show: true
                },
                renderer: $.jqplot.BarRenderer,
                showHighlight: false,
                rendererOptions: {
                    // Speed up the animation a little bit.
                    // This is a number of milliseconds. 
                    // Default for bar series is 3000. 
                    animation: {
                        speed: 2500
                    },
                    barWidth: 15,
                    barPadding: -15,
                    barMargin: 0,
                    highlightMouseOver: false
                }
            },
            {
                rendererOptions: {
                    // speed up the animation a little bit.
                    // This is a number of milliseconds.
                    // Default for a line series is 2500.
                    animation: {
                        speed: 2000
                    }
                }
            }
        ],
        axesDefaults: {
            pad: 0
        },
        axes: {
            // These options will set up the x axis like a category axis.
            xaxis: {
                renderer:$.jqplot.CategoryAxisRenderer,
                ticks:util.monthsNames,
                tickRenderer: $.jqplot.CanvasAxisTickRenderer,
                tickOptions: {
                    angle: -45,
                    fontSize: '10pt',
                    showMark: false,
                    showGridline: false
                }
            },
            yaxis: {
                tickOptions: {
                    formatString: "$%'d"
                },
                rendererOptions: {
                    forceTickAt0: true
                }
            }
        },
        grid: {
            background: '#111111'
        }
    });
};

/**
 * in order to build the pie graph dynamically as a result of chosing spacific date scope
 *   we'll create a list of item's that match the requested date scope
 *   this list later used in the Pie Graph
 * @param startDate
 * @param endDate
 * @param itemCategory
 * @param itemCost
 * @param expenseDate
 * @param index
 * @param ulLength
 */
util.addItemCostToCategoryArrayVolumeBasedOnDateScope = function (startDate, endDate, itemCategory, itemCost, expenseDate, index, ulLength){
    //at the first item reset the util.categoryArrayVolume
    if(index === 0){
        util.categoryArrayVolume.length = 0;
    }

    //if the item is in date range add its cost to the array
    //if its category does not appear in the array add it first
    if (expenseDate <= endDate && expenseDate >= startDate) {
        var categoryExists = false;
        for (var idx = 0; idx < util.categoryArrayVolume.length; idx++) {
            if (itemCategory === util.categoryArrayVolume[idx][0]) {
                util.categoryArrayVolume[idx][1] += itemCost;
                categoryExists = true;
            }
        }
        if (categoryExists === false) {
            var newCategoryVolume = [itemCategory, itemCost];
            util.categoryArrayVolume.push(newCategoryVolume);
        }
    }

    //on the last item only
    if (index === ulLength-1){
        //call for pie graph creation
        util.buildPieGraph();
    }
};

//a generic popup message injected ddynamicallyto the current page and pops up
var popupMessage = function( message, id){
    $( '#popup-' + id ).remove();
    var popup = '<div data-role="popup" data-theme="none" data-overlay-theme="a"' +
    'data-corners="false" data-tolerance="15" id="popup-' + id + '">'+ message +'</div>';
    $.mobile.pageContainer.pagecontainer('getActivePage').append(popup);
    $( '#popup-' + id ).popup().popup( "open" );

};