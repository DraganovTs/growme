export class ITaskParams{
    userId?: string='';
    status?: string = '';
    sort: string = 'createdAtDesc'; 
    pageIndex: number = 1;      
    pageSize: number = 10;      
    search?: string = '';      
     maxPageSize = 20;        
     categoryName?: string;   
  constructor() {
    this.pageSize = Math.min(this.pageSize, this.maxPageSize);
  }
}