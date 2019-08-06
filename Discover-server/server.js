const express = require('express');
var cors = require('cors');
var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;
const app = express();
app.use(cors());
app.listen(8080, () => {
  console.log('Server started!');
});

// app.use(express.static(__dirname));


app.route('/api/autoComplete').get((req, res) => {
	var zipCode = req.query.zip;
	var url = "http://api.geonames.org/postalCodeSearchJSON?postalcode_startsWith=" + zipCode + "&username=youwangd&country=US&maxRows=5";
	var success = false;
    const xmlhttp = new XMLHttpRequest();
    xmlhttp.open('GET', url, true);
    xmlhttp.onreadystatechange = () => {
      if (xmlhttp.status === 200) {
        try{
          const recommend = [];
          const response = JSON.parse(xmlhttp.responseText);
          for(let i = 0; i < response.postalCodes.length; i++) {
            recommend.push(response.postalCodes[i].postalCode);
          }
          if(!success) {
          	res.send(recommend);
          }
          success = true;
        } catch (e) {
        }
      }
    };
    xmlhttp.send();
});

app.route('/api/search').get((req, res) => {
	var keyword = req.query.Keyword;
	var category = req.query.Category;
	var newI = req.query.New;
	var used = req.query.Used;
	var unspec = req.query.Unspec;
	var local = req.query.Local;
	var free = req.query.Free;
	var distance = req.query.Distance;
	var location = req.query.Location;
	var zipInput = req.query.ZipInput;
	var curZipCode = req.query.CurZipCode;
    var url = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=YouwangD-hw6youwa-PRD-316e2f149-851b3b61&RESPONSE-DATA-FORMAT=JSON&RESTPAYLOAD&paginationInput.entriesPerPage=50&";
	url += "keywords=" + keyword + "&";
	if(category != "All categories") {
		url += "categoryId=" + category + "&";
	}
	if(location == "cur") {
		url += "buyerPostalCode=" + curZipCode + "&";
	} else {
		url += "buyerPostalCode=" + zipInput + "&";
	}
	url += "itemFilter(0).name=MaxDistance&itemFilter(0).value=" + distance + "&";
	url += "itemFilter(1).name=HideDuplicateItems&itemFilter(1).value=true&";
	url += "itemFilter(2).name=LocalPickupOnly&itemFilter(2).value=" + local + "&";
	url += "itemFilter(3).name=FreeShippingOnly&itemFilter(3).value=" + free + "&";
	if(newI == "true" || used == "true" || unspec == "true") {
		url += "itemFilter(4).name=Condition&";
		var count = 0;
		if(newI == "true") {
			url += "itemFilter(4).value(" + count + ")=New&";
			count++;
		}
		if(used == "true") {
			url += "itemFilter(4).value(" + count + ")=Used&";
			count++;
		}
		if(unspec == "true") {
			url += "itemFilter(4).value(" + count + ")=Unspecified&";
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

app.route('/api/detail').get((req, res) => {
	var itemId = req.query.ItemId;
	var url="http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=JSON&appid=YouwangD-hw6youwa-PRD-316e2f149-851b3b61&siteid=0&version=967&ItemID="+ itemId + "&IncludeSelector=Description,Details,ItemSpecifics";
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

app.route('/api/photos').get((req, res) => {
  var title = req.query.Title;
  var url = "https://www.googleapis.com/customsearch/v1?q=" + encodeURI(title) + "&cx=015981694758843230182:rhfljpksgae&imgSize=huge&imgType=news&num=8&searchType=image&key=AIzaSyCPaFcIQZHJKQMoS-9xirFeyw0iUHoHT00";
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



app.route('/api/similar').get((req, res) => {
  var itemId = req.query.ItemId;
  var url="http://svcs.ebay.com/MerchandisingService?OPERATION-NAME=getSimilarItems&SERVICE-NAME=MerchandisingService&SERVICE-VERSION=1.1.0&CONSUMER-ID=YouwangD-hw6youwa-PRD-316e2f149-851b3b61&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&itemId="+ itemId + "&maxResults=20";
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

app.route('/api/keywordSearch').get((req, res) => {
  var keyword = req.query.keyword;
  var url = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=YouwangD-hw6youwa-PRD-316e2f149-851b3b61&RESPONSE-DATA-FORMAT=JSON&RESTPAYLOAD&paginationInput.entriesPerPage=50&";
  url += "keywords=" + keyword;
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



















