import { SearchDataModel } from './model/searchData.model';
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';

// export interface ZipCode {
//   value: string;
// }

@Injectable()
export class ApiService {
  public zipCodes: any;
  public zipCodes$ = new BehaviorSubject<any>([]);

  public searchResult: any;
  public searchResult$ = new BehaviorSubject<any>([]);

  public detail: any;
  public detail$ = new BehaviorSubject<any>([]);

  public photos: any;
  public photos$ = new BehaviorSubject<any>([]);

  public similarData: any;
  public similarData$ = new BehaviorSubject<any>([]);
  //
  // public backendData: any;
  // public backendData$ = new BehaviorSubject<any>([]);

  constructor(private http: HttpClient){}

  searchProduct(searchModel: SearchDataModel) {
    let query = 'http://hw8-youwangd.appspot.com/api/search?';
    for(const key in searchModel) {
      let value = searchModel[key];
      if(key === 'Category') {
        if(value !== 'All categories') {
          if(value === 'Art') {
            value = '550';
          } else if(value === 'Baby') {
            value = '2984';
          } else if(value === 'Books') {
            value = '267';
          } else if(value === 'Clothing, Shoes & Accessories') {
            value =  '11450';
          } else if(value === 'Computer/Tablets & Networking') {
            value = '58058';
          } else if(value === 'Health & Beauty') {
            value = '26395';
          } else if(value === 'Music') {
            value = '11233';
          } else if(value === 'Video Games & Consoles') {
            value = '1249';
          }
        }
      }
      query += key + '=' + value + '&';
    }
    return this.http.get(query).subscribe(
      data => {
        this.searchResult = data;
        this.searchResult$.next(this.searchResult);
        // console.log(data);
      }
    );
  }


  autoCompleteApi(zipInput: string) {
    const query = 'http://hw8-youwangd.appspot.com/api/autoComplete?zip=' + zipInput;
    return this.http.get(query).subscribe(
      data => {
        this.zipCodes = data;
        this.zipCodes$.next(this.zipCodes);
        // console.log(this.zipCodes);
      }
    );
  }

  searchDetail(itemId: string) {
    const query = 'http://hw8-youwangd.appspot.com/api/detail?ItemId=' + itemId;
    return this.http.get(query).subscribe(
      data => {
        this.detail = data;
        this.detail$.next(this.detail);
        // console.log(this.detail);
      }
    );
  }

  searchPhotos(title: string[]) {
    // console.log(title);
    let query = 'http://hw8-youwangd.appspot.com/api/photos?Title=' + title;
    // query = encodeURIComponent(query);
    // console.log(query);
    return this.http.get(query).subscribe(
      data => {
        this.photos = data;
        this.photos$.next(this.photos);
        // console.log(this.photos);
      }
    );
  }

  searchSimilar(itemId: string) {
    const query = 'http://hw8-youwangd.appspot.com/api/similar?ItemId=' + itemId;
    return this.http.get(query).subscribe(
      data => {
        this.similarData = data;
        this.similarData$.next(this.similarData);
        // console.log(this.similarData);
      }
    );
  }

  // searchBackend(keyword: string) {
  //   const query = 'http://localhost:8080/backend/search?keyword=' + keyword;
  //   return this.http.get(query).subscribe(
  //     data => {
  //       this.backendData = data;
  //       this.backendData$.next(this.backendData);
  //       console.log(this.backendData);
  //     }
  //   );
  // }
}
