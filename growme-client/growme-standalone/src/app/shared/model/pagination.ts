export interface Pagination<T> {
    pageIndex: number;
    pageSize: number;
    count: number;
    dataList: T;
}