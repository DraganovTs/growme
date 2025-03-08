export interface IPagination<T> {
    pageIndex: number;
    pageSize: number;
    count: number;
    dataList: T;
}