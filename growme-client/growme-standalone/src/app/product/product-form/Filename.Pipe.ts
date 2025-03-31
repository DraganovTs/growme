import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filename',
  standalone: true
})
export class FilenamePipe implements PipeTransform {
  transform(value: string): string {
    if (!value) return '';
    const segments = value.split('/');
    return segments[segments.length - 1];
  }
}