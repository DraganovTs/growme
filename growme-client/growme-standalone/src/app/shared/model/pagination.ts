export interface IPagination<T> {
    pageIndex: number;
    pageSize: number;
    totalCount: number;
    dataList: T;
}