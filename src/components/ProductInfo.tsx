/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { motion } from 'motion/react';
import { Target, Star, HelpCircle, ShieldCheck, Heart, Sparkles, Megaphone, CheckSquare } from 'lucide-react';

export default function ProductInfo() {
  
  const academyFeatures = [
    {
      title: '📱 محاكي الإعلانات الذكي',
      description: 'يتيح لصاحب الصفحة معاينة الإعلانات والتصاميم، والتنبؤ الدقيق بمشاهدات وتكلفة الرسائل والطلب قبل صرف دولار واحد بالواقع.'
    },
    {
      title: '💬 محاكي الحوارات المطور',
      description: 'تدريب تفاعلي لسيناريوهات محادثات حقيقية مع الزبون العراقي لتجاوز اعتراضاته وتعلم أفضل أسلوب لإقناعه وإتمام عملية البيع بالرسائل.'
    },
    {
      title: '📊 حاسبة الأرباح والتخطيط',
      description: 'تخطيط مالي مسبق للحملات الإعلانية، وتوقع الأرباح والخسائر بدقة بالدولار والدينار العراقي لضمان استقرار ومتابعة نمو المشروع.'
    },
    {
      title: '🔍 كاشف تسرب الأرباح',
      description: 'أداة ذكية لمعرفة أسباب خسارة وتشتت الأرباح وتحديد الأضرار الناتجة عن الطلبات المرجوعة والإلغاءات، وتوفير حلول فورية لتفاديها.'
    },
    {
      title: '🗓️ خطة الـ 30 يوماً العملية',
      description: 'مسار عملي واضح ومنهجي مرسوم خطوة بخطوة للوصول إلى أول 100 طلب حقيقي لصفحة أو مشروع متجر إلكتروني مبتدئ.'
    }
  ];

  const targetAudiences = [
    '🛍️ أصحاب المشاريع الصغيرة والمتاجر الإلكترونية النشطة.',
    '👗 صفحات الملابس، العطور، المكياج، الإكسسوارات، والمنزلية.',
    ' هدايا، طباعة حسب الطلب، وصناعات يدوية متميزة.',
    '🚀 الأشخاص الي يريدون يبدون مشروعهم الأونلاين من الصفر وبدون أي خبرة.',
    '💸 الصفحات الي تصرف مبالغ جبيرة على التمويل وما تحصل نتائج مرضية ومقنعة.',
    '🎓 الراغبين بتعلم التسويق بدلاً من الاعتماد على مروجين ودفع اشتراكات شهرية.'
  ];

  return (
    <section id="about-product" className="bg-slate-50 py-20 text-right border-b border-slate-200/50">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="mx-auto max-w-3xl text-center mb-16 space-y-4">
          <h2 className="text-3xl font-extrabold tracking-tight sm:text-4xl text-slate-900 font-sans">
            فهم الخدمة: شنو هو كورس IQ Ads Academy؟ 🎓
          </h2>
          <p className="text-slate-600 text-lg leading-relaxed font-sans">
            حتى تقنع أصحاب البيجات بسهولة، لازم تفهم القيمة العظيمة الي راح يحصلون عليها ببرنامجنا التفاعلي، وليش هو مختلف عن أي كورس ثاني بالعراق:
          </p>
          <div className="mx-auto h-1 w-20 rounded-full bg-indigo-600" />
        </div>

        <div className="grid grid-cols-1 gap-12 lg:grid-cols-12 lg:items-start">
          
          {/* Why buy from us / Competitive edge */}
          <div className="lg:col-span-5 rounded-3xl border border-slate-200 bg-white p-8 space-y-6 shadow-sm">
            <div className="flex flex-row-reverse items-center gap-2.5 text-amber-500">
              <Star className="h-6 w-6 fill-amber-500" />
              <h3 className="text-xl font-bold font-sans text-slate-900">شنو الميزة التنافسية الي تبيعها؟</h3>
            </div>

            <p className="text-sm text-slate-600 leading-relaxed font-sans">
              أصحاب البيجات تعبوا من الكورسات الي تبيعلهم فيديوهات مسجلة قديمة أو معلومات نظرية موجودة باليوتيوب. برنامج <strong className="text-indigo-600 font-bold">IQ Ads Academy</strong> يثور على هالشي بتقديم نظام عملي متكامل:
            </p>

            <ul className="space-y-4 text-xs text-slate-600 font-sans">
              <li className="flex flex-row-reverse items-start gap-2">
                <ShieldCheck className="h-4 w-4 text-indigo-600 shrink-0 mt-0.5" />
                <span><strong className="text-slate-800">موجه خصيصاً للعراق:</strong> يشرح طبيعة الزبون العراقي وسوق التوصيل العراقي والتعامل مع المشاكل المحلية.</span>
              </li>
              <li className="flex flex-row-reverse items-start gap-2">
                <ShieldCheck className="h-4 w-4 text-indigo-600 shrink-0 mt-0.5" />
                <span><strong className="text-slate-800">تعليم + أدوات برمجية:</strong> يحصل العميل على محاكيات حقيقية تتدرب وتخطط من خلالها أرباحها وخسائرها.</span>
              </li>
              <li className="flex flex-row-reverse items-start gap-2">
                <ShieldCheck className="h-4 w-4 text-indigo-600 shrink-0 mt-0.5" />
                <span><strong className="text-slate-800">توفير هائل للمال:</strong> العميل يتعلم شلون يدير تسويقه بنفسه وباحترافية تامة بدلاً من دفع 300$ شهرياً لشركات تسويق ما تحقق نتائج.</span>
              </li>
              <li className="flex flex-row-reverse items-start gap-2">
                <ShieldCheck className="h-4 w-4 text-indigo-600 shrink-0 mt-0.5" />
                <span><strong className="text-slate-800">ضمان وتحديث مستمر:</strong> محتوى متطور يواكب تغييرات خوارزميات ميتا وإنستغرام أولاً بأول لضمان البقاء في القمة.</span>
              </li>
            </ul>

            <div className="rounded-2xl border border-indigo-100 bg-indigo-50/50 p-4 text-center">
              <span className="text-xs text-indigo-600 block mb-1">العميل لا يشتري مجرد معلومات</span>
              <span className="text-sm font-bold text-indigo-800 block font-sans">هو يشتري نظاماً كاملاً ومضموناً لإنقاذ مشروعه!</span>
            </div>
          </div>

          {/* Features Detail */}
          <div className="lg:col-span-7 space-y-8">
            <div className="flex flex-row-reverse items-center gap-2.5 text-indigo-600">
              <Megaphone className="h-6 w-6" />
              <h3 className="text-xl font-bold font-sans">المميزات الأساسية للبرنامج</h3>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              {academyFeatures.map((feat, index) => (
                <div 
                  key={index} 
                  className={`rounded-2xl border border-slate-200 bg-white p-6 shadow-sm hover:border-indigo-300 transition-all ${
                    index === 4 ? 'sm:col-span-2 border-indigo-150 bg-indigo-50/30' : ''
                  }`}
                >
                  <h4 className="font-bold text-slate-900 text-base font-sans mb-2">{feat.title}</h4>
                  <p className="text-xs text-slate-500 leading-relaxed font-sans">{feat.description}</p>
                </div>
              ))}
            </div>

            {/* Target Audience List */}
            <div className="rounded-3xl border border-slate-200 bg-white p-6 md:p-8 mt-4 shadow-sm">
              <div className="flex flex-row-reverse items-center gap-2.5 text-amber-500 mb-6">
                <Target className="h-6 w-6" />
                <h3 className="text-lg font-bold font-sans text-slate-900">منو جمهورك المستهدف بدقة؟</h3>
              </div>
              <p className="text-xs text-slate-500 font-sans leading-relaxed mb-4">
                أي صفحة انستغرام عاصية بالتسويق أو تبحث عن زبائن، هي زبون محتمل الك. دور وركز على البيجات الي تعرض المنتجات التالية:
              </p>
              
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {targetAudiences.map((aud, index) => (
                  <div key={index} className="flex flex-row-reverse items-start gap-2 text-xs text-slate-600 font-sans">
                    <CheckSquare className="h-4 w-4 text-indigo-600 shrink-0 mt-0.5" />
                    <span>{aud}</span>
                  </div>
                ))}
              </div>
            </div>

          </div>

        </div>

      </div>
    </section>
  );
}
