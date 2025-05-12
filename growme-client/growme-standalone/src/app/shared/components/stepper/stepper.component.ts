import { CdkStepper } from '@angular/cdk/stepper';
import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-stepper',
  imports: [],
  templateUrl: './stepper.component.html',
  styleUrl: './stepper.component.scss',
  providers: [{provide: CdkStepper, useExisting: StepperComponent}]
})
export class StepperComponent extends CdkStepper implements OnInit{


  @Input() linearModeSelected: boolean = false;

  ngOnInit(): void {
    this.linear = this.linearModeSelected;
  }
  onClick(index: number) {
    this.selectedIndex = index;
  }

}
