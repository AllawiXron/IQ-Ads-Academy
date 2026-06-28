/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { MonitorPlay, AlertOctagon, Heart, MessageCircle, Send, ShoppingBag, Landmark, Coins, HelpCircle } from 'lucide-react';

export default function ProductDemo() {
  const [activeTab, setActiveTab] = useState<'simulator' | 'leakage'>('simulator');

  // Simulator State
  const [pageName, setPageName] = useState('MyStyle_IQ');
  const [category, setCategory] = useState('clothing');
  const [dailyBudget, setDailyBudget] = useState(10); // USD

  // Leakage State
  const [shippedOrders, setShippedOrders] = useState(100);
  const [itemPrice, setItemPrice] = useState(25000); // IQD
  const [returnRate, setReturnRate] = useState(20); // %

  const formatNumber = (num: number) => {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  };

  // Ads Simulator calculations (mocked but mathematically proportional)
  const getSimulatedMetrics = () => {
    let multiplier = 1.0;
    if (category === 'clothing') multiplier = 1.1;
    if (category === 'perfumes') multiplier = 1.0;
    if (category === 'accessories') multiplier = 0.9;
    if (category === 'custom_print') multiplier = 1.2;

    const reach = Math.round(dailyBudget * 1200 * multiplier);
    const messages = Math.round(dailyBudget * 8 * multiplier);
    const avgCostPerMsg = (dailyBudget / messages).toFixed(2);
    const predictedSales = Math.round(messages * 0.15); // 15% conversion

    return { reach, messages, avgCostPerMsg, predictedSales };
  };

  const metrics = getSimulatedMetrics();

  // Leakage calculations
  const deliveryFee = 5000; // Average Iraqi delivery fee
  const returnedOrdersCount = Math.round((shippedOrders * returnRate) / 100);
  const successfulOrdersCount = shippedOrders - returnedOrdersCount;
  
  const grossSales = shippedOrders * itemPrice;
  const deliveryCostWasted = returnedOrdersCount * deliveryFee;
  const lostRevenue = returnedOrdersCount * itemPrice;
  const actualRevenue = successfulOrdersCount * itemPrice;
  const netEarningsLoss = deliveryCostWasted + (returnedOrdersCount * (itemPrice * 0.3)); // assuming 30% margin loss

  return (
    <section id="demo" className="bg-white py-20 text-right border-b border-slate-200/50">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="mx-auto max-w-3xl text-center mb-16 space-y-4">
          <h2 className="text-3xl font-extrabold tracking-tight sm:text-4xl text-slate-900 font-sans">
            جرّب بنفسك الأدوات التفاعلية للبرنامج 🎮
          </h2>
          <p className="text-slate-600 text-lg leading-relaxed font-sans">
            أنت لا تبيع "فيديوهات مسجلة" أو "كتاب بي دي اف"، بل تبيع برنامجاً تفاعلياً متكاملاً يحتوي على أدوات وحاسبات متطورة. جرّب عينات منها هنا لتقدر تشرحها لزبائنك:
          </p>
          
          {/* Tabs */}
          <div className="flex justify-center pt-4">
            <div className="inline-flex w-full max-w-md sm:w-auto rounded-xl bg-slate-100 p-1 border border-slate-200">
              <button
                id="tab-leakage"
                onClick={() => setActiveTab('leakage')}
                className={`flex-1 sm:flex-initial text-center rounded-lg px-3 sm:px-6 py-2 sm:py-2.5 text-xs sm:text-sm font-bold transition-all cursor-pointer ${
                  activeTab === 'leakage'
                    ? 'bg-indigo-600 text-white shadow-sm'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                کاشف تسرب الأرباح 🔍
              </button>
              <button
                id="tab-sim"
                onClick={() => setActiveTab('simulator')}
                className={`flex-1 sm:flex-initial text-center rounded-lg px-3 sm:px-6 py-2 sm:py-2.5 text-xs sm:text-sm font-bold transition-all cursor-pointer ${
                  activeTab === 'simulator'
                    ? 'bg-indigo-600 text-white shadow-sm'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                محاكي إعلانات إنستغرام 📱
              </button>
            </div>
          </div>
        </div>

        {/* Tab Content Panel */}
        <div className="rounded-3xl border border-slate-200 bg-slate-50/50 p-6 md:p-10 shadow-sm backdrop-blur-sm">
          <AnimatePresence mode="wait">
            {activeTab === 'simulator' ? (
              <motion.div
                key="simulator"
                initial={{ opacity: 0, y: 15 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -15 }}
                transition={{ duration: 0.3 }}
                className="grid grid-cols-1 gap-12 lg:grid-cols-12 lg:items-center"
              >
                
                {/* Mock Phone Preview */}
                <div className="lg:col-span-5 flex justify-center order-2 lg:order-1">
                  <div className="w-full max-w-[340px] rounded-[40px] border-[10px] border-slate-200 bg-white shadow-xl overflow-hidden aspect-[9/18] flex flex-col justify-between text-right">
                    
                    {/* Phone Top Notch */}
                    <div className="h-6 bg-slate-100 flex items-center justify-between px-6 text-[10px] text-slate-500 font-mono">
                      <span>09:36</span>
                      <div className="h-3 w-16 bg-slate-200 rounded-full" />
                      <span>5G 🔋</span>
                    </div>

                    {/* Instagram Header */}
                    <div className="p-3 border-b border-slate-100 flex flex-row-reverse items-center justify-between text-slate-800 text-xs">
                      <div className="flex flex-row-reverse items-center gap-2">
                        <div className="h-8 w-8 rounded-full bg-gradient-to-tr from-yellow-500 to-purple-600 p-0.5">
                          <div className="h-full w-full rounded-full bg-white flex items-center justify-center font-bold text-[10px]">
                            {pageName.substring(0, 2).toUpperCase()}
                          </div>
                        </div>
                        <div>
                          <span className="font-bold block text-slate-900 font-mono">@{pageName}</span>
                          <span className="text-[10px] text-slate-400 block -mt-0.5">ممّول • Sponsored</span>
                        </div>
                      </div>
                      <span className="font-black text-slate-300 text-xs">•••</span>
                    </div>

                    {/* Instagram Image Placeholder based on Category */}
                    <div className="bg-slate-50 aspect-square flex flex-col items-center justify-center text-slate-500 relative p-4 overflow-hidden border-b border-slate-100">
                      <div className="absolute top-2 right-2 rounded bg-slate-900/80 px-2 py-0.5 text-[9px] text-yellow-400 font-bold">
                        محاكاة العرض والطلب
                      </div>
                      {category === 'clothing' && <span className="text-5xl">👗👘👔</span>}
                      {category === 'perfumes' && <span className="text-5xl">🧴🧼🛁</span>}
                      {category === 'accessories' && <span className="text-5xl">🎁💎💍</span>}
                      {category === 'custom_print' && <span className="text-5xl">🎨👕☕</span>}
                      {category === 'electronics' && <span className="text-5xl">🔌📱💻</span>}
                      {category === 'general' && <span className="text-5xl">💼📊📈</span>}
                      
                      <span className="text-xs text-slate-400 mt-4 font-mono">معاينة الإعلان التفاعلي</span>
                    </div>

                    {/* Instagram Actions */}
                    <div className="p-3 bg-white">
                      <div className="flex flex-row-reverse justify-between items-center text-slate-600 text-xs mb-2">
                        <div className="flex flex-row-reverse items-center gap-3">
                          <Heart className="h-5 w-5 text-red-500 fill-red-500" />
                          <MessageCircle className="h-5 w-5" />
                          <Send className="h-5 w-5" />
                        </div>
                        <ShoppingBag className="h-5 w-5 text-indigo-600" />
                      </div>
                      <span className="text-[10px] text-slate-500 font-mono block mb-1">حصل على {metrics.reach} مشاهدة تفاعلية</span>
                      
                      {/* Caption */}
                      <p className="text-[11px] leading-relaxed text-slate-600">
                        <span className="font-bold text-slate-900 ml-1 font-mono">@{pageName}</span>
                        تريد تزيد مبيعات متجرك بطريقة مضمونة وتقطع الخسائر؟ اشترك ببرنامجنا التفاعلي..
                      </p>

                      {/* Call To Action Button */}
                      <div className="mt-3 rounded bg-indigo-600 text-white text-center font-bold py-1.5 text-xs hover:bg-indigo-700 transition-colors">
                        إرسال رسالة / Send Message
                      </div>
                    </div>

                  </div>
                </div>

                {/* Input Fields & Explanation */}
                <div className="lg:col-span-7 space-y-6 order-1 lg:order-2">
                  <div>
                    <span className="inline-block text-xs font-bold text-indigo-700 bg-indigo-50 border border-indigo-100 rounded-full px-3.5 py-1 block mb-2 w-fit">أداة محاكي الإعلانات 📱</span>
                    <h3 className="text-2xl font-bold text-slate-900 font-sans">معاينة وتوقع النتائج قبل الصرف</h3>
                    <p className="text-sm text-slate-600 leading-relaxed font-sans mt-2">
                      في الكورس العادي، يعلمون العميل طريقتين تمويل ويتركوه يخسر فلوسه ويجرب بالواقع. في برنامجنا التفاعلي، العميل يدخل ميزانيته، اسم صفحته، ويحدد المجال.. والبرنامج يسويله محاكاة كاملة للنتائج ويحسبله التكلفة المتوقعة للرسالة قبل لا يصرف دولار واحد بالفيسبوك!
                    </p>
                  </div>

                  {/* Simulator Controls */}
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 rounded-2xl bg-white p-6 border border-slate-200 shadow-sm">
                    
                    <div className="space-y-2">
                      <label className="text-xs font-bold text-slate-700 block">معرّف الصفحة (إنستغرام):</label>
                      <input 
                        type="text" 
                        value={pageName}
                        onChange={(e) => setPageName(e.target.value)}
                        className="w-full text-left rounded-xl bg-slate-50 border border-slate-200 px-4 py-2 text-slate-800 font-mono text-sm focus:border-indigo-600 focus:outline-none"
                      />
                    </div>

                    <div className="space-y-2">
                      <label className="text-xs font-bold text-slate-700 block">مجال العمل والمنتجات:</label>
                      <select 
                        value={category}
                        onChange={(e) => setCategory(e.target.value)}
                        className="w-full rounded-xl bg-slate-50 border border-slate-200 px-4 py-2 text-slate-800 text-xs focus:border-indigo-600 focus:outline-none"
                        style={{ direction: 'rtl' }}
                      >
                        <option value="clothing">ملابس وأزياء 👗</option>
                        <option value="perfumes">عطور ومواد تجميل 🧴</option>
                        <option value="accessories">إكسسوارات وهدايا 🎁</option>
                        <option value="custom_print">طباعة حسب الطلب 🎨</option>
                        <option value="electronics">إلكترونيات وأجهزة 🔌</option>
                        <option value="general">مجالات أخرى 💼</option>
                      </select>
                    </div>

                    <div className="sm:col-span-2 space-y-2 pt-2">
                      <div className="flex justify-between flex-row-reverse text-xs font-bold text-slate-700">
                        <span>الميزانية اليومية للتمويل:</span>
                        <span className="text-indigo-600 font-mono">{dailyBudget} دولار / يومياً</span>
                      </div>
                      <input 
                        type="range" 
                        min="5" 
                        max="100" 
                        step="5"
                        value={dailyBudget}
                        onChange={(e) => setDailyBudget(parseInt(e.target.value))}
                        className="w-full h-2 bg-slate-100 rounded-lg appearance-none cursor-pointer accent-indigo-600"
                        style={{ direction: 'rtl' }}
                      />
                    </div>

                  </div>

                  {/* Simulator Outputs */}
                  <div className="rounded-2xl bg-white border border-slate-200 shadow-sm p-6 grid grid-cols-2 gap-4 sm:grid-cols-4">
                    <div className="text-center">
                      <span className="text-xs text-slate-500 block mb-1">الوصول المتوقع</span>
                      <span className="text-lg font-black text-slate-800 font-mono">~{formatNumber(metrics.reach)}</span>
                    </div>
                    <div className="text-center border-r border-slate-100">
                      <span className="text-xs text-slate-500 block mb-1">عدد الرسائل</span>
                      <span className="text-lg font-black text-indigo-600 font-mono">~{metrics.messages}</span>
                    </div>
                    <div className="text-center border-r border-slate-100">
                      <span className="text-xs text-slate-500 block mb-1">سعر الرسالة المتوقع</span>
                      <span className="text-lg font-black text-amber-600 font-mono">${metrics.avgCostPerMsg}</span>
                    </div>
                    <div className="text-center border-r border-slate-100">
                      <span className="text-xs text-slate-500 block mb-1">مبيعات متوقعة</span>
                      <span className="text-lg font-black text-indigo-600 font-mono">{metrics.predictedSales} مبيعات</span>
                    </div>
                  </div>

                </div>

              </motion.div>
            ) : (
              <motion.div
                key="leakage"
                initial={{ opacity: 0, y: 15 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -15 }}
                transition={{ duration: 0.3 }}
                className="grid grid-cols-1 gap-12 lg:grid-cols-12 lg:items-center"
              >
                
                {/* Visual Report Card */}
                <div className="lg:col-span-5 flex justify-center order-2 lg:order-1">
                  <div className="w-full max-w-[360px] rounded-3xl border border-slate-200 bg-white p-6 text-right space-y-6 relative overflow-hidden shadow-lg">
                    
                    {/* Danger top border line */}
                    <div className="absolute top-0 right-0 left-0 h-1.5 bg-gradient-to-l from-indigo-600 to-amber-500" />
                    
                    <div className="flex flex-row-reverse items-center gap-3 border-b border-slate-100 pb-4">
                      <div className="rounded-full bg-red-50 p-2 text-red-600 shrink-0">
                        <AlertOctagon className="h-6 w-6" />
                      </div>
                      <div>
                        <h4 className="font-bold text-slate-900 font-sans text-sm">كاشف تسرب الأرباح</h4>
                        <span className="text-[10px] text-slate-400 font-mono">تقرير الخسائر والمبيعات المهدرة</span>
                      </div>
                    </div>

                    <div className="space-y-4">
                      <div className="flex justify-between items-center border-b border-slate-100 pb-2 flex-row-reverse">
                        <span className="text-xs text-slate-500">إجمالي المبيعات الإسمية:</span>
                        <span className="text-sm font-bold text-slate-700 font-mono">{formatNumber(grossSales)} د.ع</span>
                      </div>
                      
                      <div className="flex justify-between items-center border-b border-slate-100 pb-2 flex-row-reverse">
                        <span className="text-xs text-slate-500">عدد الطلبات الراجعة ({returnRate}%):</span>
                        <span className="text-sm font-bold text-red-600 font-mono">{returnedOrdersCount} طلب راجع</span>
                      </div>

                      <div className="flex justify-between items-center border-b border-slate-100 pb-2 flex-row-reverse">
                        <span className="text-xs text-slate-500">أجور توصيل ضائعة (للشركة):</span>
                        <span className="text-sm font-bold text-red-600 font-mono">{formatNumber(deliveryCostWasted)} د.ع</span>
                      </div>

                      <div className="flex justify-between items-center border-b border-slate-100 pb-2 flex-row-reverse">
                        <span className="text-xs text-slate-500">قيمة منتجات معطلة بالتوصيل:</span>
                        <span className="text-sm font-bold text-amber-600 font-mono">{formatNumber(lostRevenue)} د.ع</span>
                      </div>

                      <div className="flex justify-between items-center pt-2 flex-row-reverse bg-red-50 p-3 rounded-xl border border-red-100">
                        <span className="text-xs font-bold text-red-700">صافي الخسائر الحقيقية:</span>
                        <span className="text-base font-black text-red-600 font-mono">~{formatNumber(netEarningsLoss)} د.ع</span>
                      </div>
                    </div>

                    <div className="rounded-xl bg-slate-50 p-3 text-center border border-slate-100">
                      <span className="text-[10px] text-slate-500 leading-relaxed font-sans block">
                        💡 المشترك بـ IQ Ads Academy يتعلم شلون يقلل نسبة الارتجاع إلى أقل من 5% باستعمال نظام حماية وهيكلة ذكية.
                      </span>
                    </div>

                  </div>
                </div>

                {/* Input Controls and explanation */}
                <div className="lg:col-span-7 space-y-6 order-1 lg:order-2">
                  <div>
                    <span className="inline-block text-xs font-bold text-amber-700 bg-amber-50 border border-amber-100 rounded-full px-3.5 py-1 block mb-2 w-fit font-sans">أداة كاشف تسرب الأرباح 🔍</span>
                    <h3 className="text-2xl font-bold text-slate-900 font-sans">معرفة أين يضيع تعب صاحب الصفحة</h3>
                    <p className="text-sm text-slate-600 leading-relaxed font-sans mt-2">
                      أغلب أصحاب البيجات يفرحون بعدد الطلبات المرسلة، بس ينصدمون بنهاية الشهر أنو ما عدهم أرباح حقيقية بجيوبهم. هاي الأداة التفاعلية مدمجة بالبرنامج وتحسبلهم بدقة مبالغ أجور التوصيل الضائعة والمنتجات المعطلة بالتوصيل، وتعلمهم خطوات حلها لإنقاذ أرباحهم.
                    </p>
                  </div>

                  {/* Controls */}
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 rounded-2xl bg-white p-6 border border-slate-200 shadow-sm">
                    
                    <div className="space-y-2">
                      <div className="flex justify-between flex-row-reverse text-xs text-slate-700">
                        <label className="font-bold">عدد الطلبات المرسلة شهرياً:</label>
                        <span className="font-mono text-indigo-600 font-bold">{shippedOrders} طلب</span>
                      </div>
                      <input 
                        type="range" 
                        min="20" 
                        max="1000" 
                        step="20"
                        value={shippedOrders}
                        onChange={(e) => setShippedOrders(parseInt(e.target.value))}
                        className="w-full h-1.5 bg-slate-100 rounded-lg appearance-none cursor-pointer accent-indigo-600"
                        style={{ direction: 'rtl' }}
                      />
                    </div>

                    <div className="space-y-2">
                      <div className="flex justify-between flex-row-reverse text-xs text-slate-700">
                        <label className="font-bold">سعر البيع المتوسط للمنتج:</label>
                        <span className="font-mono text-indigo-600 font-bold">{formatNumber(itemPrice)} د.ع</span>
                      </div>
                      <input 
                        type="range" 
                        min="10000" 
                        max="150000" 
                        step="5000"
                        value={itemPrice}
                        onChange={(e) => setItemPrice(parseInt(e.target.value))}
                        className="w-full h-1.5 bg-slate-100 rounded-lg appearance-none cursor-pointer accent-indigo-600"
                        style={{ direction: 'rtl' }}
                      />
                    </div>

                    <div className="sm:col-span-2 space-y-2 pt-2">
                      <div className="flex justify-between flex-row-reverse text-xs font-bold text-slate-700">
                        <span>نسبة الارتجاع والإلغاء المتوقعة بالطلب العراقي:</span>
                        <span className="text-red-600 font-mono font-black">{returnRate}% من الشحنات</span>
                      </div>
                      <input 
                        type="range" 
                        min="5" 
                        max="60" 
                        step="5"
                        value={returnRate}
                        onChange={(e) => setReturnRate(parseInt(e.target.value))}
                        className="w-full h-2 bg-slate-100 rounded-lg appearance-none cursor-pointer accent-red-500"
                        style={{ direction: 'rtl' }}
                      />
                    </div>

                  </div>

                  {/* Highlights of Solution */}
                  <div className="rounded-2xl border border-indigo-100 bg-indigo-50/50 p-5">
                    <h4 className="font-bold text-indigo-700 text-sm mb-2 font-sans">الحل التفاعلي داخل IQ Ads Academy:</h4>
                    <p className="text-xs text-slate-600 leading-relaxed font-sans">
                      البرنامج يمنح صاحب المتجر نظاماً متكاملاً لإدارة شركات التوصيل، ويتضمن "محاكي الحوارات" الذي يدرب خدمة العملاء على تأكيد الطلب بأسلوب يمنع إلغائه عند التوصيل، مما يقلل المرتجعات لأقل من 5% ويوفر ملايين الدنانير الضائعة شهرياً.
                    </p>
                  </div>

                </div>

              </motion.div>
            )}
          </AnimatePresence>
        </div>

      </div>
    </section>
  );
}
