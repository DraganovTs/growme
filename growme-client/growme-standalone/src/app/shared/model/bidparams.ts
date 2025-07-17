import { HttpParams } from "@angular/common/http";

export class IBidParams {
  userId?: string = '';
  status?: string = '';
  taskId?: string = '';
  sort: string = 'createdAtDesc';
  pageIndex: number = 1;
  pageSize: number = 10;
  maxPageSize: number = 50;

  constructor(params?: Partial<IBidParams>) {
    if (params) {
      Object.assign(this, params);
    }
    // Ensure pageSize doesn't exceed maxPageSize
    this.pageSize = Math.min(this.pageSize, this.maxPageSize);
  }

  toHttpParams(): HttpParams {
    let params = new HttpParams()
      .set('pageIndex', this.pageIndex.toString())
      .set('pageSize', this.pageSize.toString())
      .set('sort', this.sort);

    if (this.userId) params = params.set('userId', this.userId);
    if (this.status) params = params.set('status', this.status);
    if (this.taskId) params = params.set('taskId', this.taskId);

    return params;
  }
}