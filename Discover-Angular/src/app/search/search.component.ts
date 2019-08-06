import {Component, OnInit} from '@angular/core';
import { SearchDataModel } from '../model/searchData.model';
import {ApiService} from '../api.service';
import {trigger, transition, animate, style} from '@angular/animations';
// import * as $ from 'jquery';
@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
  animations: [
    trigger('resultAnimation', [
      transition(':enter', [
        // style({transform: 'translateX(-100%)'}),
        style({visibility: 'hidden'}),
        // style({marginRight: '100vw'}),
        // animate('200ms', style({transform: 'translateX(0%)'}))
        animate('2500ms 200ms', style({visibility: 'visible'}))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({transform: 'translateX(100%)'}))
      ])
    ]),
    trigger('progressAnimation', [
      transition(':enter', [
        style({transform: 'translateX(-100%)'}),
        animate('200ms', style({transform: 'translateX(0%)'}))
      ]),
      // transition(':leave', [
      //   animate('200ms ease-in', style({transform: 'translateX(100%)'}))
      // ])
    ])
  ]
})
export class SearchComponent implements OnInit {
  keyword = '';
  zipInput = '';
  distance = '';
  category = 'All categories';
  new = false;
  used = false;
  unspec = false;
  local = false;
  free = false;
  location = 'cur';
  curZipCode = '';
  searchData : SearchDataModel;
  constructor(private apiService: ApiService) {}
  suggestZipCode: any;
  wishSelected = false;
  result: any = null;
  progress = 0;
  page = 1;
  totalPage = 0;
  rowSelected = -1;
  wishRowSelected = -1;
  triggerDetail = false;
  triggerWishDetail = false;
  showDetail = false;
  detailTab = 'product';
  detailReturn: any = null;
  productSpecifics = [];
  productPhotos = [];
  selectedImage = '';
  photosReturn = [];
  selectedItemData: any = null;
  similarReturn: any = null;
  similarReturnCopy: any = null;
  noSimilarRecord = false;
  showMore = false;
  showItem = 5;
  sort = 'Default';
  order = 'Ascending';
  wishListStorage = window.localStorage;
  storageItems = [];
  wishListTotalCost = 0;
  onClear = false;
  toggle = false;
  timer = 0;

  clear() {
    this.keyword = '';
    this.zipInput = '';
    this.distance = '';
    this.category = 'All categories';
    this.new = false;
    this.used = false;
    this.unspec = false;
    this.local = false;
    this.free = false;
    this.location = 'cur';
    this.wishSelected = false;
    this.result = {};
    this.progress = 0;
    this.page = 1;
    this.totalPage = 0;
    this.rowSelected = -1;
    this.wishRowSelected = -1;
    this.triggerDetail = false;
    this.triggerWishDetail = false;
    this.showDetail = false;
    this.detailTab = 'product';
    this.detailReturn = {};
    this.productSpecifics = [];
    this.productPhotos = [];
    this.selectedImage = '';
    this.photosReturn = [];
    this.selectedItemData = {};
    this.similarReturn = {};
    this.similarReturnCopy = {};
    this.noSimilarRecord = false;
    this.showMore = false;
    this.showItem = 5;
    this.sort = 'Default';
    this.order = 'Ascending';
    this.onClear = true;
  }

  ngOnInit() {
    // $('body').addClass('df');
    this.getLoc();
    this.storageItems = Object.values(this.wishListStorage).map( value => JSON.parse(value));
    if(this.storageItems.length > 0) {
      this.wishListTotalCost = this.storageItems.reduce((total, item) => total + parseFloat(item.sellingStatus[0].currentPrice[0].__value__), 0);
    }
    this.apiService.zipCodes$.subscribe(
      data => {
        this.suggestZipCode = data;
        // console.log(this.suggestZipCode);
      }
    );
    this.apiService.searchResult$.subscribe(
      data => {
        this.result = data;
        if(this.result.hasOwnProperty('findItemsAdvancedResponse')) {
          // console.log(this.result);
          if(this.result.findItemsAdvancedResponse[0].ack == 'Success') {
            this.totalPage = this.result.findItemsAdvancedResponse[0].searchResult[0]['@count'];
            this.totalPage = Math.ceil(this.totalPage / 10);
            // console.log(this.totalPage);
          }
          // this.progressTime();
          setTimeout(() => {
            this.progress = 100;
          }, 300);
        }
      }
    );

    this.apiService.detail$.subscribe(
    data => {
      this.detailReturn = data;
      if(this.detailReturn.hasOwnProperty('Item') && this.detailReturn.Item.hasOwnProperty('ItemSpecifics') && this.detailReturn.Item.ItemSpecifics.NameValueList.length > 0) {
        this.productSpecifics = this.detailReturn.Item.ItemSpecifics.NameValueList;
      }
      if(this.detailReturn.hasOwnProperty('Item') && this.detailReturn.Item.hasOwnProperty('PictureURL')) {
        this.productPhotos = this.detailReturn.Item.PictureURL;
      }
      // setTimeout(() => {
      //     this.progress = 100;
      //   }, 300);
    });

    this.apiService.photos$.subscribe(
    data => {
      if(data.hasOwnProperty('items')) {
        this.photosReturn = data.items;
      }
    });

    this.apiService.similarData$.subscribe(
      data => {
        if(data.hasOwnProperty('getSimilarItemsResponse') && data.getSimilarItemsResponse.hasOwnProperty('itemRecommendations')) {
          this.similarReturn = data.getSimilarItemsResponse.itemRecommendations.item;
          this.similarReturnCopy = JSON.parse(JSON.stringify(this.similarReturn));
          // console.log(this.similarReturn);
          if(this.similarReturn.length == 0) {
            this.noSimilarRecord = true;
          } else {
            this.noSimilarRecord = false;
          }
        } else {
          this.noSimilarRecord = true;
        }
      });
  }

  getLoc() {
    const xmlhttp = new XMLHttpRequest();
    const url = 'http://ip-api.com/json';
    xmlhttp.open('GET', url, true);
    xmlhttp.onreadystatechange = () => {
      if (xmlhttp.status == 200) {
        try{
          const response = JSON.parse(xmlhttp.responseText);
          this.curZipCode = response["zip"];
        } catch (e) {
        }
      }
    };
    xmlhttp.send();

  }

  displayFn(str ?: string): string | undefined {
    return str ? str : undefined;
  }

  onSubmit() {
    if(this.distance === '') {
      this.distance = '10';
    }
    this.searchData = new SearchDataModel(
      this.keyword,
      this.category,
      this.new,
      this.used,
      this.unspec,
      this.local,
      this.free,
      this.distance,
      this.location,
      this.zipInput,
      this.curZipCode);
    this.apiService.searchProduct(this.searchData);
    this.progress = 50;
    this.showDetail = false;
    this.rowSelected = -1;
    this.wishRowSelected = -1;
    this.page = 1;
    this.wishSelected = false;
    this.onClear = false;
    this.totalPage = 0;
  }

  clearZip() {
    this.zipInput = '';
  }

  autoComplete() {
    if(this.zipInput.length >= 3) {
      this.apiService.autoCompleteApi(this.zipInput);
    }
  }

  onSelectResult() {
    this.wishSelected = false;
  }

  onSelectWish() {
    this.wishSelected = true;
    this.showDetail = false;
  }


  fixString(title: string): string {
    let str = '';
    if(title.length > 35) {
      const words = title.split(' ');
      let i = 0;
      while(str.length < 35) {
        if((str + words[i]).length > 35) {
          return str.substring(0, str.length - 1) + '...';
        } else {
          str += words[i] + ' ';
          i++;
        }
      }
      return str.substring(0, str.length - 1) + '...';
    }
    return title;
  }

  onSelectItem(itemId: string, index: number, title: string, selectData: any) {
    if(this.wishSelected) {
      this.wishRowSelected = index;
      this.rowSelected = -1;
      this.triggerWishDetail = true;
    } else {
      this.rowSelected = index;
      this.wishRowSelected = -1;
      this.triggerDetail = true;
    }

    this.apiService.searchDetail(itemId);
    // this.progress = 50;
    this.showDetail = true;
    this.apiService.searchPhotos([title]);
    this.selectedItemData = selectData;
    this.apiService.searchSimilar(itemId);
    this.order = 'Ascending';
    this.sort = 'Default';
    this.toggle = true;
    this.detailTab = 'product';
    setTimeout(() => {
      this.toggle = false;
    }, 200);
  }

  onSelectPage(p: number) {
    if(p < 1) {
      p = 1;
    }
    if(p > this.totalPage) {
      p = this.totalPage;
    }
    this.page = p;
    // console.log(this.page);
  }

  toggleWishListItem(itemId: string, itemData: any) {
    // if(this.wishList.hasOwnProperty(itemId)) {
    //   delete this.wishList[itemId];
    // } else {
    //   this.wishList[itemId] = true;
    // }
    // console.log(this.wishList);
    if(this.wishListStorage.getItem(itemId) == null) {
      this.wishListStorage.setItem(itemId, JSON.stringify(itemData));
    } else {
      this.wishListStorage.removeItem(itemId);
    }
    this.storageItems = Object.values(this.wishListStorage).map( value => JSON.parse(value));
    if(this.storageItems.length > 0) {
      this.wishListTotalCost = this.storageItems.reduce((total, item) => total + parseFloat(item.sellingStatus[0].currentPrice[0].__value__), 0);
    } else {
      this.wishListTotalCost = 0;
    }
    // console.log(this.storageItems);
    // console.log(this.wishListTotalCost);
  }

  onSelectDetailTab(tab: string) {
    this.detailTab = tab;
  }


  openDetail() {
    this.showDetail = true;
    this.toggle = true;
    setTimeout(() => {
      this.toggle = false;
    }, 200);
  }

  closeDetail() {
    this.showDetail = false;
    this.detailTab = 'product';
    this.toggle = true;
    setTimeout(() => {
      this.toggle = false;
    }, 200);
  }

  // clickImage(url: string) {
  //   this.selectedImage = url;
  // }
  //
  // closeImage() {
  //   this.selectedImage = '';
  //   // console.log('close clicked!');
  // }

  filterColor(starColor: string): string {
    const index = starColor.indexOf('Shooting');
    if(index != -1) {
      return starColor.substring(0, index);
    }
    return starColor;
  }

  extractDays(str: string): string {
    const reg = /.*P(.*)D.*/;
    if(reg.exec(str) !== null) {
      return reg.exec(str)[1];
    }
    return 'N/A';
  }

  showMoreSimilar() {
    this.showMore = true;
    this.showItem = 20;
  }

  showLessSimilar() {
    this.showMore = false;
    this.showItem = 5;
  }



  selectSort() {
    if(this.order == 'Ascending') {
      if(this.sort == 'Default') {
        this.similarReturn = JSON.parse(JSON.stringify(this.similarReturnCopy));
        this.order = 'Ascending';
      } else if(this.sort == 'Product name') {
        this.similarReturn.sort((a, b) => (a.title).localeCompare(b.title));
      } else if(this.sort == 'Days Left') {
        this.similarReturn.sort((a, b) => parseFloat(this.extractDays(a.timeLeft)) - parseFloat(this.extractDays(b.timeLeft)));
      } else if(this.sort == 'Prices') {
        this.similarReturn.sort((a, b) => {
          let numA = 0;
          let numB = 0;
          if(a.hasOwnProperty('buyItNowPrice') && a.buyItNowPrice.__value__ > 0) {
            numA = parseFloat(a.buyItNowPrice.__value__);
          } else {
            numA = parseFloat(a.currentPrice.__value__);
          }
          if(b.hasOwnProperty('buyItNowPrice') && b.buyItNowPrice.__value__ > 0) {
            numB = parseFloat(b.buyItNowPrice.__value__);
          } else {
            numB = parseFloat(b.currentPrice.__value__);
          }
          return numA - numB;
        });
      } else if(this.sort == 'Shipping Cost') {
        this.similarReturn.sort((a, b) => parseFloat(a.shippingCost.__value__) - parseFloat(b.shippingCost.__value__));
      }
    } else {
      if(this.sort == 'Default') {
        this.similarReturn = JSON.parse(JSON.stringify(this.similarReturnCopy));
        // this.order = 'Ascending';
      } else if(this.sort == 'Product name') {
        this.similarReturn.sort((a, b) => (b.title).localeCompare(a.title));
      } else if(this.sort == 'Days Left') {
        this.similarReturn.sort((a, b) => parseFloat(this.extractDays(b.timeLeft)) - parseFloat(this.extractDays(a.timeLeft)));
      } else if(this.sort == 'Prices') {
        this.similarReturn.sort((a, b) => {
          let numA = 0;
          let numB = 0;
          if(a.hasOwnProperty('buyItNowPrice') && a.buyItNowPrice.__value__ > 0) {
            numA = parseFloat(a.buyItNowPrice.__value__);
          } else {
            numA = parseFloat(a.currentPrice.__value__);
          }
          if(b.hasOwnProperty('buyItNowPrice') && b.buyItNowPrice.__value__ > 0) {
            numB = parseFloat(b.buyItNowPrice.__value__);
          } else {
            numB = parseFloat(b.currentPrice.__value__);
          }
          return numB - numA;
        });
      } else if(this.sort == 'Shipping Cost') {
        this.similarReturn.sort((a, b) => parseFloat(b.shippingCost.__value__) - parseFloat(a.shippingCost.__value__));
      }
    }
  }
}
