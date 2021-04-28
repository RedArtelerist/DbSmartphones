package space.fedorenko.dbsmartphones;

public class CompanyItem {
    private String companyName;
    private int logoImage;

    public CompanyItem(String companyName, int logoImage) {
        this.companyName = companyName;
        this.logoImage = logoImage;
    }
    public String getCompanyName() {
        return companyName;
    }
    public int getLogoImage() {
        return logoImage;
    }
}
