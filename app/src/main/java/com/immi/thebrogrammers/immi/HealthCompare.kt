package com.immi.thebrogrammers.immi


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.data.Set
import com.anychart.enums.HoverMode
import com.anychart.enums.LabelsOverlapMode
import com.anychart.enums.TooltipDisplayMode
import com.anychart.enums.TooltipPositionMode


/**
 * A simple [Fragment] subclass.
 *
 */
class HealthCompare : Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_health_compare, container, false)
    val anyChartView = view.findViewById<com.anychart.AnyChartView>(R.id.any_chart_view)

    val barChart: Cartesian = AnyChart.bar()

    barChart.animation(true)
    barChart.padding(10, 20, 5, 20)

//    barChart.yScale().minimum(-100)
//
//    barChart.yScale().maximum(100)

    barChart.yAxis(0).labels().format(
      "function() {\n" +
        "    return Math.abs(this.value).toLocaleString();\n" +
        "  }")
    barChart.yAxis((0))
    barChart.yScale().maximum(100)
    barChart.yScale().minimum(0)
    barChart.xAxis(0).labels().width(150)
    barChart.yAxis(0).title("Percentage")
    barChart.xAxis(0).overlapMode(LabelsOverlapMode.ALLOW_OVERLAP)
    barChart.barsPadding(10)
//    val xAxis1: Linear = barChart.xAxis(1)
//    xAxis1.enabled(true)
//    xAxis1.orientation(Orientation.RIGHT)
//    xAxis1.overlapMode(LabelsOverlapMode.ALLOW_OVERLAP)



    barChart.interactivity().hoverMode(HoverMode.BY_X)

    barChart.tooltip()
      .title(false)
      .separator(false)
      .displayMode(TooltipDisplayMode.SEPARATED)
      .positionMode(TooltipPositionMode.POINT)
      .useHtml(true)
      .fontSize(12)
      .offsetX(5)
      .offsetY(0)
      .format(
        "function() {\n" +
          "      return '<span style=\"color: #D9D9D9\">$</span>' + Math.abs(this.value).toLocaleString();\n" +
          "    }")
    barChart.xScroller(true)

    val seriesData = mutableListOf<DataEntry>()
    val activity: CompareCities = activity as CompareCities
    val cityName1 = activity.getCity1()
    val cityName2 = activity.getCity2()
    val category = activity.getCategory()
    barChart.title("Comparison by $category")
    barChart.xScroller().allowRangeChange(false)
    val scale = barChart.xScale()
    barChart.xZoom().setToPointsCount(6, false, scale)

    val city1 = ImmIDatabase.getCityByName(cityName1)!!
    val city2 = ImmIDatabase.getCityByName(cityName2)!!
    for ((k, v) in city1.qIndices) {
      if (ImmIDatabase.categoryMap[k] == category) {
        var valCity1 = (city1.qIndices[k]!! /* 100*/).toInt()
        var valCity2 = (city2.qIndices[k]!! /* 100*/).toInt()
        if (category == "Cost of living" && k != "Cost of living index") {
          val maxVal = (valCity1 + valCity2).toDouble()
          if (maxVal != 0.0) {
            valCity1 = (valCity1 * 100.0 / maxVal).toInt()
            valCity2 = (valCity2 * 100.0 / maxVal).toInt()
          }
//          valCity1 *= 50;
//          valCity2 *= 50;

        }
        if (k == "CO2 Emission Index" || k == "Time Exp. Index") {
          valCity1 /= 100
          valCity2 /= 100

        }


        seriesData.add(CustomDataEntry(k, valCity1, 1 * valCity2))
      }
    }


//    seriesData.add(CustomDataEntry("Nail polish", 5376, -229))
//    seriesData.add(CustomDataEntry("Eyebrow pencil", 10987, -932))
//    seriesData.add(CustomDataEntry("Rouge", 7624, -5221))
//    seriesData.add(CustomDataEntry("Lipstick", 8814, -256))
//    seriesData.add(CustomDataEntry("Eyeshadows", 8998, -308))
//    seriesData.add(CustomDataEntry("Eyeliner", 9321, -432))
//    seriesData.add(CustomDataEntry("Foundation", 8342, -701))
//    seriesData.add(CustomDataEntry("Lip gloss", 6998, -908))
//    seriesData.add(CustomDataEntry("Mascara", 9261, -712))

    val set = Set.instantiate()
    set.data(seriesData)
    val series1Data = set.mapAs("{ x: 'x', value: 'value' }")
    val series2Data = set.mapAs("{ x: 'x', value: 'value2' }")

    val series1 = barChart.bar(series1Data)
    series1.name(cityName1)
//      .color("HotPink")
//    series1.tooltip()
//      .position("right")
//      .anchor(Anchor.LEFT_CENTER)

    val series2 = barChart.bar(series2Data)
    series2.name(cityName2)
//    series2.tooltip()
//      .position("left")
//      .anchor(Anchor.RIGHT_CENTER)
//
    barChart.legend().enabled(true)
    barChart.legend().inverted(true)
    barChart.legend().fontSize(13.0)
    barChart.legend().padding(0.0, 0.0, 20.0, 0.0)

    anyChartView.setChart(barChart)

    return view
  }

  private inner class CustomDataEntry internal constructor(x: String, value: Number, value2: Number) : ValueDataEntry(x, value) {
    init {
      setValue("value2", value2)
    }
  }
}
