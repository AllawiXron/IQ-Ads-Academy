/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Sparkles, HelpCircle, Trophy, TrendingUp, CheckCircle2, ChevronLeft } from 'lucide-react';

export default function Calculator() {
  const [salesPerDay, setSalesPerDay] = useState(3);
  const commission = 5000;

  const dailyEarnings = salesPerDay * commission;
  const weeklyEarnings = dailyEarnings * 7;
  const monthlyEarnings = dailyEarnings * 30;

  // Custom levels and badges based on performance
  const getLevelDetails = (sales: number) => {
    if (sales <= 2) {
      return {
        badge: '🌱 مروج طموح',
        description: 'بداية ممتازة! بجهد بسيط جداً لا يتجاوز ساعة باليوم تكدر تطلع مصروفك وزيادة وتأمن احتياجاتك.',
        color: 'from-indigo-500 to-indigo-600',
        textColor: 'text-indigo-700',
        bg: 'bg-indigo-50 border-indigo-200'
      };
    } else if (sales <= 5) {
      return {
        badge: '🔥 مروج نشط ومحترف',
        description: 'مستوى رائع جداً! هذا يعادل أو يتجاوز راتب وظيفة حكومية أو أهلية كاملة بالعراق، والكل من ورا موبايلك وبساعات فراغك.',
        color: 'from-amber-500 to-amber-600',
        textColor: 'text-amber-700',
        bg: 'bg-amber-50 border-amber-200'
      };
    } else if (sales <= 10) {
      return {
        badge: '👑 مروج ذهبي فائق الأداء',
        description: 'أنت بطل! هذا الدخل يدخلك في فئة الرواتب المتميزة بالعراق. صفحات إنستغرام ومتاجر التواصل بالآلاف بانتظارك ومبيعاتك راح تنفجر.',
        color: 'from-yellow-500 to-amber-600',
        textColor: 'text-amber-800',
        bg: 'bg-yellow-50 border-yellow-200'
      };
    } else {
      return {
        badge: '🚀 شريك ملكي استثنائي',
        description: 'مستوى الأساطير! هذا مو مجرد ترويج، أنت تدير وكالة تسويقية ميدانية خاصة بيك وتحقق ثروة ممتازة شهرياً.',
        color: 'from-indigo-600 to-indigo-700',
        textColor: 'text-indigo-800',
        bg: 'bg-indigo-100 border-indigo-300'
      };
    }
  };

  const level = getLevelDetails(salesPerDay);

  const formatNumber = (num: number) => {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  };

  return (
    <section id="calculator" className="bg-slate-50 py-20 text-right border-b border-slate-200/50">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="mx-auto max-w-3xl text-center mb-16 space-y-4">
          <h2 className="text-3xl font-extrabold tracking-tight sm:text-4xl text-slate-900 font-sans">
            حاسبة الأرباح التفاعلية 💸
          </h2>
          <p className="text-slate-600 text-lg leading-relaxed font-sans">
            حرّك المؤشر في الأسفل وشوف شكد تكدر تطلع فلوس زينة شهرياً بمجرد تواصلك وإقناعك لعدد بسيط من الصفحات باليوم!
          </p>
          <div className="mx-auto h-1 w-20 rounded-full bg-indigo-600" />
        </div>

        <div className="grid grid-cols-1 gap-12 lg:grid-cols-12 lg:items-stretch">
          
          {/* Output Results Card */}
          <div className="lg:col-span-7 flex flex-col justify-between rounded-3xl border border-slate-200 bg-white p-8 shadow-sm relative">
            
            <div className="space-y-8">
              <div className="flex flex-row-reverse items-center justify-between border-b border-slate-100 pb-6">
                <div>
                  <h3 className="text-xl font-bold text-slate-900 font-sans">تقديرات أرباحك الصافية</h3>
                  <p className="text-xs text-slate-500 mt-1">العمولات مضمونة وتتحول الك فوراً عبر زين كاش</p>
                </div>
                <div className={`rounded-full px-4 py-1.5 text-xs font-bold border ${level.bg} ${level.textColor}`}>
                  {level.badge}
                </div>
              </div>

              {/* Earnings Breakdown */}
              <div className="grid grid-cols-1 gap-4 sm:gap-6 sm:grid-cols-3">
                
                {/* Daily */}
                <div className="rounded-2xl bg-slate-50 p-4 sm:p-6 border border-slate-200/60 text-center relative overflow-hidden group">
                  <div className="absolute top-0 right-0 left-0 h-1 bg-gradient-to-l from-indigo-500/20 to-indigo-600/20" />
                  <span className="text-xs text-slate-500 block mb-2 font-sans">ربحك باليوم الواحد</span>
                  <div className="text-2xl font-black text-indigo-600 font-mono tracking-tight">
                    {formatNumber(dailyEarnings)} <span className="text-sm font-sans text-slate-600">د.ع</span>
                  </div>
                  <span className="text-[10px] text-slate-400 mt-1 block">مقابل {salesPerDay} مبيعات</span>
                </div>

                {/* Weekly */}
                <div className="rounded-2xl bg-slate-50 p-4 sm:p-6 border border-slate-200/60 text-center relative overflow-hidden group">
                  <div className="absolute top-0 right-0 left-0 h-1 bg-gradient-to-l from-amber-500/20 to-amber-600/20" />
                  <span className="text-xs text-slate-500 block mb-2 font-sans">ربحك في الأسبوع</span>
                  <div className="text-2xl font-black text-amber-600 font-mono tracking-tight">
                    {formatNumber(weeklyEarnings)} <span className="text-sm font-sans text-slate-600">د.ع</span>
                  </div>
                  <span className="text-[10px] text-slate-400 mt-1 block">خلال 7 أيام</span>
                </div>

                {/* Monthly */}
                <div className="rounded-2xl bg-slate-50 p-4 sm:p-6 border border-slate-200/80 text-center relative overflow-hidden group scale-100 sm:scale-105 shadow-md shadow-indigo-600/5">
                  <div className="absolute top-0 right-0 left-0 h-1 bg-indigo-600" />
                  <span className="text-xs text-slate-800 block mb-2 font-bold font-sans">ربحك الشهري المتوقع</span>
                  <div className="text-3xl font-black text-indigo-700 font-mono tracking-tight">
                    {formatNumber(monthlyEarnings)} <span className="text-sm font-sans text-slate-800">د.ع</span>
                  </div>
                  <span className="text-[10px] text-indigo-600 mt-1 block font-bold">بدون أي التزام بساعات عمل!</span>
                </div>

              </div>

              {/* Dynamic Level Description */}
              <AnimatePresence mode="wait">
                <motion.div 
                  key={salesPerDay}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -10 }}
                  className="rounded-2xl border border-slate-100 bg-slate-50/50 p-5 flex flex-row-reverse items-start gap-4"
                >
                  <div className="mt-1 rounded-full bg-indigo-50 p-2 text-indigo-600 shrink-0">
                    <Trophy className="h-5 w-5" />
                  </div>
                  <div>
                    <h4 className="font-bold text-slate-800 text-sm font-sans mb-1">تقييم الأداء والمستوى</h4>
                    <p className="text-xs text-slate-600 leading-relaxed font-sans">{level.description}</p>
                  </div>
                </motion.div>
              </AnimatePresence>
            </div>

            {/* Next Milestone Tips */}
            <div className="mt-8 border-t border-slate-100 pt-6 space-y-3">
              <h5 className="text-xs font-bold text-slate-400 uppercase tracking-wider">نصيحة لتسهيل المبيعات وزيادة الأرباح:</h5>
              <div className="flex flex-row-reverse items-center gap-2 text-xs text-slate-600 font-sans">
                <CheckCircle2 className="h-4 w-4 text-indigo-600 shrink-0" />
                <span>البيجات الي تمول إعلانات وتواجه منافسة قوية هي الأسهل بالإقناع.</span>
              </div>
              <div className="flex flex-row-reverse items-center gap-2 text-xs text-slate-600 font-sans">
                <CheckCircle2 className="h-4 w-4 text-indigo-600 shrink-0" />
                <span>انسخ نصوص الرسائل الجاهزة من حقيبة المروج المرفقة بالأسفل لتوفير الوقت.</span>
              </div>
            </div>

          </div>

          {/* Calculator Inputs Card */}
          <div className="lg:col-span-5 rounded-3xl border border-slate-200 bg-white p-8 shadow-sm flex flex-col justify-center">
            
            <div className="space-y-6">
              <div className="flex items-center justify-between flex-row-reverse">
                <div className="flex items-center gap-2 text-indigo-600">
                  <TrendingUp className="h-5 w-5" />
                  <h3 className="text-xl font-bold text-slate-900 font-sans">توقّع مبيعاتك</h3>
                </div>
                <HelpCircle className="h-5 w-5 text-slate-400 cursor-pointer hover:text-slate-600" />
              </div>
              
              <p className="text-xs text-slate-500 leading-relaxed font-sans">
                كم بيعة أو اشتراك بالبرنامج التفاعلي تكدر تنجز باليوم الواحد؟ (صفحة ملابس، صفحة عطور، صفحة إكسسوارات وغيرها بالإنستغرام والفيسبوك)
              </p>

              {/* BIG Dynamic Slider Output */}
              <div className="py-6 text-center">
                <span className="text-7xl font-black text-slate-900 font-mono">{salesPerDay}</span>
                <span className="text-xl font-bold text-slate-400 font-sans mr-2">مبيعات / يومياً</span>
              </div>

              {/* Range Slider */}
              <div className="space-y-2">
                <input 
                  type="range" 
                  min="1" 
                  max="20" 
                  value={salesPerDay}
                  onChange={(e) => setSalesPerDay(parseInt(e.target.value))}
                  className="w-full h-2.5 bg-slate-100 rounded-lg appearance-none cursor-pointer accent-indigo-600 focus:outline-none"
                  style={{ direction: 'rtl' }}
                />
                <div className="flex justify-between text-xs text-slate-400 font-mono">
                  <span>20 مبيعة</span>
                  <span>10 مبيعات</span>
                  <span>1 مبيعة</span>
                </div>
              </div>

              {/* Commission tag */}
              <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4 text-center">
                <span className="text-xs text-slate-500 block mb-1">العمولة الثابتة على كل اشتراك</span>
                <span className="text-lg font-bold text-indigo-600 font-sans">5,000 دينار عراقي صافي</span>
              </div>

              {/* Action */}
              <button 
                id="btn-crm-link"
                onClick={() => {
                  const el = document.getElementById('toolkit');
                  if (el) el.scrollIntoView({ behavior: 'smooth' });
                }}
                className="w-full flex items-center justify-center gap-2 rounded-full border border-indigo-200 bg-indigo-50 py-3.5 px-4 font-bold text-indigo-600 transition-all hover:bg-indigo-600 hover:text-white shadow-sm cursor-pointer text-sm"
              >
                <span>تصفح حقيبة نصوص ورسائل الترويج والبدء بالعمل 🎒</span>
              </button>

            </div>

          </div>

        </div>

      </div>
    </section>
  );
}
