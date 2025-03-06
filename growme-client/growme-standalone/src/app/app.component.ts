import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavBarComponent } from './core/nav-bar/nav-bar.component';
import { ShopComponent } from './shop/shop.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet , NavBarComponent , CommonModule , ShopComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  pageTitle = 'growme-standalone';
}
