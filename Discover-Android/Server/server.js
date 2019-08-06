// changed server part

// add one route for nearby_disabled_search
app.route('/api/nearby_disabled_search').get((req, res) => {
  var keyword = req.query.Keyword;
  var category = req.query.Category;
  var newI = req.query.New;
  var used = req.query.Used;
  var unspec = req.query.Unspec;
  var local = req.query.Local;
  var free = req.query.Free;
    var url = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=YouwangD-hw6youwa-PRD-316e2f149-851b3b61&RESPONSE-DATA-FORMAT=JSON&RESTPAYLOAD&paginationInput.entriesPerPage=50&";
  url += "keywords=" + keyword + "&";
  if(category != "All categories") {
    url += "categoryId=" + category + "&";
  }
  url += "itemFilter(0).name=HideDuplicateItems&itemFilter(0).value=true&";
  url += "itemFilter(1).name=LocalPickupOnly&itemFilter(1).value=" + local + "&";
  url += "itemFilter(2).name=FreeShippingOnly&itemFilter(2).value=" + free + "&";
  if(newI == "true" || used == "true" || unspec == "true") {
    url += "itemFilter(3).name=Condition&";
    var count = 0;
    if(newI == "true") {
      url += "itemFilter(3).value(" + count + ")=New&";
      count++;
    }
    if(used == "true") {
      url += "itemFilter(3).value(" + count + ")=Used&";
      count++;
    }
    if(unspec == "true") {
      url += "itemFilter(3).value(" + count + ")=Unspecified&";
    }
  }
  url += "outputSelector(0)=SellerInfo&outputSelector(1)=StoreInfo";
  var success = false;
  const xmlhttp = new XMLHttpRequest();
  xmlhttp.open('GET', url, true);
  xmlhttp.onreadystatechange = () => {
    if (xmlhttp.status === 200) {
      try{
        const response = JSON.parse(xmlhttp.responseText);
        if(!success) {
          res.send(response);
        }
        success = true;
      } catch (e) {
      }
    }
  };
  xmlhttp.send();
});




















