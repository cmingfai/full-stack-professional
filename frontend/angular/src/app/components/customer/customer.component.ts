import {Component, OnInit} from '@angular/core';
import {CustomerService} from "../../services/customer/customer.service";
import {CustomerDTO} from "../../models/customer-dto";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {ConfirmationService, ConfirmEventType, MessageService} from "primeng/api";

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.scss']
})
export class CustomerComponent implements OnInit{
  display=false;
  customers: CustomerDTO[]=[];
  customer: CustomerRegistrationRequest={};
  operation: 'create' | 'update'='create';

  constructor(private customerService:CustomerService,
              private messageService:MessageService,
              private confirmationService:ConfirmationService) {
  }

  ngOnInit(): void {
     this.findAllCustomers();
  }

  private findAllCustomers():void {
    this.customerService.findAll().subscribe({
      next:(data)=> {
        this.customers=data;
        console.log(data);
      }
    });
  }

  save(customer: CustomerRegistrationRequest) {
    if (customer) {
      if (this.operation==='create'){
        this.customerService.registerCustomer(customer).subscribe({
        next:()=> {
          // console.log("customer saved!");
          this.display=false;
          this.findAllCustomers();
          this.customer={};
          this.messageService.add({
            severity: 'success',
            summary: 'Customer saved',
            detail: `Customer ${customer.name} is successfully saved.`
           });
         }
        });
      } else if (this.operation==='update') {
        this.customerService.updateCustomer(customer.id,customer).subscribe({
          next:()=> {
            // console.log("customer updated!");
            this.display=false;
            this.findAllCustomers();
            this.customer={};
            this.messageService.add({
              severity: 'success',
              summary: 'Customer updated',
              detail: `Customer ${customer.name} is successfully updated.`
            });
          }
        });
      }
    }

  }

  deleteCustomer(customer:CustomerDTO) {
     console.log("delete customer: "+customer.name);

    this.confirmationService.confirm({
      message: `Are you sure you want to delete ${customer.name}? You can't undo this action afterwards.`,
      header: 'Delete customer',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        console.log(`Delete action accepted.`)

        this.customerService.deleteCustomer(customer.id).subscribe({
          next:()=> {
            console.log("customer deleted!");
            this.findAllCustomers();
            this.messageService.add({
              severity: 'success',
              summary: 'Delete Customer',
              detail: `Customer ${customer.name} has been successfully deleted.`
            });
          },error:(err) => {
            console.log(err);
            this.messageService.add({
              severity: 'danger',
              summary: 'Delete Customer',
              detail: `Fail to delete customer ${customer.name}.`
            });
          }
        });


      },
      reject: () => {
        console.log("Delete action rejected.")
      }
    });

    console.log('exit onDeleteCustomer');
  }

  updateCustomer(customerDTO: CustomerDTO) {
    console.log(customerDTO);
    this.display=true;
    this.operation="update";
    this.customer=customerDTO;
  }

  createCustomer() {
    this.display=true;
    this.operation="create";
    this.customer={};
  }

  cancel() {
    this.display=false;
    this.operation="create";
    this.customer={};
  }
}
