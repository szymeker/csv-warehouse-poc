package pl.szymeker.csvwarehouse.dimension

data class GroupDimension(val columnName: String) {
    override fun toString(): String {
        return columnName
    }
}