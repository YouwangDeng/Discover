export class SearchDataModel {
  constructor(
    public Keyword: string,
    public Category: string,
    public New: boolean,
    public Used: boolean,
    public Unspec: boolean,
    public Local: boolean,
    public Free: boolean,
    public Distance: string,
    public Location: string,
    public ZipInput: string,
    public CurZipCode: string
  ) {
  }
}
