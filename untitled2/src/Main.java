import javax.script.ScriptException;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

public class Main {


    // Создаем объект класса XYSeriesCollection для добавления в него серий (массив пар (x, y))
    public static XYSeriesCollection dataset = new XYSeriesCollection();
    // Создаем объект класса XYSeriesCollection для добавления в него серий (массив пар (x, y))


    // Функия для инициализации нескольких серий. Каждая серия нужна для реализации точек разрыва
    public static XYSeriesCollection Sozdanie_series(int i, Double[] list, Double xmin,
                                                     int xmax, int ymin, int ymax) {

        Double k;
        boolean est_razriv = false;


        // Создаю объект класса XYSeries для построения графиков
        XYSeries series = new XYSeries(xmin.toString());
        // Создаю объект класса XYSeries для построения графиков


        for(k = xmin; k <= xmax; k+=0.1){

            // Значение функции в точке k
            Double y = list[i];
            ++i;
            // Значение функции в точке k


            // Проверка на точки разрыва =========================================================>
            // Если нету точек разрыва, то добавляем в серию координаты
            if(!Proverka_na_razriv(y)){
                // Задаем область значения
                // list[k] = NaN, если sqrt(Negative)
                if(!y.isNaN() && y >= ymin && y <= ymax) {
                    series.add(k, y);
                }
            }
            // Если есть точки разрыва, то заканчиваем цикл и рекурсивно вызываем эту же функцию
            else {
                est_razriv = true;
                break;
            }
            // Проверка на точки разрыва =========================================================>

        }

        dataset.addSeries(series);

        if(est_razriv && k != xmax) {
            return Sozdanie_series(i, list, k + 0.1d, xmax, ymin, ymax);
        } else {
            return dataset;
        }
    }
    // Функия для инициализации нескольких серий. Каждая серия нужна для реализации точек разрыва


    // Проверка на точки разрыва. Если есть разрыв - true, нету - false
    public static boolean Proverka_na_razriv(Double y) {
        double plus_inf = Double.POSITIVE_INFINITY;
        double minus_inf = Double.NEGATIVE_INFINITY;
        if (y == plus_inf || y == minus_inf) {
            return true;
        } else {
            return false;
        }
    }
    // Проверка на точки разрыва. Если есть разрыв - false, нету - true


    // Возвращаю строку, где все возведения в степень я заменяю на Math.pow()
    public static String Stepen_to_mathpow(String uravnenie) {

        // Переменные для основания, показателя и индекс первого "^"
        String osnovanie_stepeni = "";
        String pokazatel_stepeni = "";
        int index_stepeni_v_stroke = uravnenie.indexOf("^");
        // Переменные для основания, показателя и индекс первого "^"


        // Работа с ОСНОВАНИЕМ степени =================================================================================>

        // Смотрим, что ПЕРЕД "^"
        boolean est_skobka_osn;
        // Если ПЕРЕД "^" скобка, то в основание будет содержимое скобок
        if(uravnenie.charAt(index_stepeni_v_stroke - 1) == ')'){
            est_skobka_osn = true;
            int chislo_skobok = 0;
            boolean proverka = true;
            int j = index_stepeni_v_stroke - 2;
            while(proverka) {
                if(uravnenie.charAt(j) == ')'){
                    ++chislo_skobok;
                } else {
                    if(uravnenie.charAt(j) == '(' && chislo_skobok == 0){
                        osnovanie_stepeni = uravnenie.substring(j + 1, index_stepeni_v_stroke - 1);
                        proverka = false;
                    }
                    if(uravnenie.charAt(j) == '(' && chislo_skobok > 0){
                        --chislo_skobok;
                    }
                }
                --j;
            }
        }
        // Если ПЕРЕД "^" нет скобки, то в основание будет число (аргумент функции)
        else {
            est_skobka_osn = false;
            int count = 1;
            int a = index_stepeni_v_stroke;
            char b = uravnenie.charAt(a - count);
            while(b != '(' && b != ' ' && b != '*' && b != '/' && b != '+' && b!= '-' && (a - count) > -1){
                osnovanie_stepeni += b;
                count++;
                if(a - count != -1){
                    b = uravnenie.charAt(a - count);
                }
            }
        }
        // Работа с ОСНОВАНИЕМ степени =================================================================================>


        // Работа с ПОКАЗАТЕЛЕМ степени ================================================================================>
        // Смотрим, что ПОСЛЕ "^"
        boolean est_skobka_pok;
        // Если ПОСЛЕ "^" скобка, то в показателе будет содержимое скобок
        if(uravnenie.charAt(index_stepeni_v_stroke + 1) == '(') {
            est_skobka_pok = true;
            int kolvo_skobok = 0;
            boolean proverka = true;
            int j = index_stepeni_v_stroke + 2;
            while(proverka){
                if(uravnenie.charAt(j) == '('){
                    ++kolvo_skobok;
                } else {
                    if(uravnenie.charAt(j) == ')' && kolvo_skobok == 0){
                        pokazatel_stepeni = uravnenie.substring(index_stepeni_v_stroke + 2, j);
                        proverka = false;
                    }
                    if(uravnenie.charAt(j) == ')' && kolvo_skobok > 0){
                        --kolvo_skobok;
                    }
                }
                ++j;
            }
        }
        // Если ПОСЛЕ "^" нет скобки, то в показателе будет число (аргумент функции)
        else{
            est_skobka_pok = false;
            int count = 1;
            int a = index_stepeni_v_stroke;
            char b = uravnenie.charAt(a + count);
            while(b != ' ' && b != '*' && b != '/' && b != '+' && b!= '-' && a + count <= uravnenie.length() - 1){
                pokazatel_stepeni += b;
                count++;
                if(a + count <= uravnenie.length() - 1){
                    b = uravnenie.charAt(a + count);
                }
            }
        }
        // Работа с ПОКАЗАТЕЛЕМ степени ================================================================================>


        // На данном этапе я имею основание степени вида "a + b*x + (...) + ..."
        // И показатель степни такого же вида
        // Далее идет создания Math.pow(osnovanie, pokazatel)
        // И замена показателя и основания на Math.pow в uravnenie

        // Деалею переменную типа String вида Math.pow(osnovanie_stepeni, pokazatel_stepeni)
        String MathPow = "Math.pow(osn, pok)".replace("osn", osnovanie_stepeni);
        MathPow = MathPow.replace("pok", pokazatel_stepeni);
        // Деалею переменную типа String вида Math.pow(osnovanie_stepeni, pokazatel_stepeni)

        // Создаю строку для замены среза со степенью в uravnenie
        String stroka_dlya_zameni = "";
        if(est_skobka_osn){
            stroka_dlya_zameni += "(";
            stroka_dlya_zameni += osnovanie_stepeni + ")";
        }
        else {
            stroka_dlya_zameni += osnovanie_stepeni;
        }

        if(est_skobka_pok){
            stroka_dlya_zameni += "^(";
            stroka_dlya_zameni += pokazatel_stepeni + ")";
        }
        else {
            stroka_dlya_zameni += "^" + pokazatel_stepeni;
        }
        // Создаю строку для замены среза со степенью в uravnenie


        // Получаю новую строку, где степень заменена на Math.pow
        // <============================================================================================================>
        uravnenie = uravnenie.replace(stroka_dlya_zameni, MathPow);
        // <============================================================================================================>
        // Получаю новую строку, где степень заменена на Math.pow


        // Проверяем на то, есть ли еще знак "^"

        // Если знака степени нет в строке, то возвращаем эту строку
        if(uravnenie.indexOf("^") == -1){
            return uravnenie;
        }
        // Если знак степени есть в строке, то делаем рекурсивный вызов функции, в которую передаем проверяемую строку
        else{
            return Stepen_to_mathpow(uravnenie);
        }

        // Проверяем на то, есть ли еще знак "^"

    }


    public static void main(String[] args) throws ScriptException {


        // Подключаем eval из JavaScript
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Object result = engine.eval("");
        // Подключаем eval из JavaScript


        // Вводим уравнение
        String uravnenie = args[0];
        // Вводим уравнение


        // Вводим ограничения оси абсцисс (x)
        Integer x_min = Integer.parseInt(args[1]);
        Integer x_max = Integer.parseInt(args[2]);
        // Вводим ограничения оси абсцисс (x)


        // Вводим ограничения оси ординат (y)
        Integer y_min = Integer.parseInt(args[3]);
        Integer y_max = Integer.parseInt(args[4]);
        // Вводим ограничения оси ординат (y)


        // Смотрим, есть ли "^" в уравнении -> избавляемся от него с помощью Math.pow
        String uravnenie_s_mathpow = "";
        if(uravnenie.indexOf("^") != -1) {
            uravnenie_s_mathpow = Stepen_to_mathpow(uravnenie);
        } else {
            uravnenie_s_mathpow = uravnenie;
        }
        // Смотрим, есть ли "^" в уравнении -> избавляемся от него с помощью Math.pow


        // Создаем массив от x_min*10 до x_max*10 (область определения с шагом 1), куда помещаем значения функции на этом промежутке
        int index = 0;
        Double [] znacheniya_funkcii = new Double[(x_max - x_min)*10 + 1];
        // Создаем массив от x_min*10 до x_max*10 (область определения с шагом 1), куда помещаем значения функции на этом промежутке


        // Делаем так тк "особенность" джавы делает 0.0 равным 0.01293438 >:O
        for(int i = x_min*10; i <= x_max*10; i+=1) {
            String to_replace = String.valueOf(i/10) + "." + String.valueOf(Math.abs(i%10));

            // Делаем проверку тк целое от деления -9/10 дает 0 без знака
            // Если -9 <= i < 0, то добавляем знак "-" перед нулем
            if(i/10 == 0 && i < 0){
                to_replace = "-0" + "." + String.valueOf(Math.abs(i%10));
            }
            // Если 0 <= i < 10, то не добавляем знак "-" перед нулем
            if(i/10 == 0 && i >= 0){
                to_replace = "0" + "." + String.valueOf(Math.abs(i%10));
            }
            // Делаем проверку тк целое от деления -9/10 дает 0 без знака


            // Создаем переменную типа Object
            // В нее записываем результат выполнения всех арфиметических действий и методов классов, содержащихся в строке
            Object y_object = engine.eval(uravnenie_s_mathpow.replace("x", to_replace));
            Double y = Double.parseDouble(y_object.toString());
            znacheniya_funkcii[index] = y;
            ++index;
            // Создаем переменную типа Object
            // В нее записываем результат выполнения всех арфиметических действий и методов классов, содержащихся в строке


        }
        // Делаем так тк "особенность" джавы делает 0.0 равным 0.01293438 >:O


        // Проверка на использование второго графика
        boolean the_switch = true;
        // Проверка на использование второго графика


        dataset = Sozdanie_series(0, znacheniya_funkcii, Double.parseDouble(x_min.toString()), x_max, y_min, y_max);


        JFreeChart chart = ChartFactory.createXYLineChart("y = " + uravnenie, "x", "y", dataset, PlotOrientation.VERTICAL, true, true, true);

        // Создаем фрейм
        JFrame frame = new JFrame("MinimalStaticChart");

        // Помещаем график на фрейм
        frame.getContentPane().add(new ChartPanel(chart));
        frame.setSize(4000,3000);
        frame.show();

    }
}