use master
exec sp_dboption 'lportal6100', 'allow nulls by default' , true
go

exec sp_dboption 'lportal6100', 'select into/bulkcopy/pllsort' , true
go

use lportal6100





